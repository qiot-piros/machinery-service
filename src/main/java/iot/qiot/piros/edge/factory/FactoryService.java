package iot.qiot.piros.edge.factory;

import com.fasterxml.jackson.databind.ObjectMapper;
import iot.qiot.piros.edge.MachineryServiceApplication;
import iot.qiot.piros.edge.configuration.MachineryConfiguration;
import iot.qiot.piros.edge.core.model.SystemInformation;
import iot.qiot.piros.edge.factory.client.FactoryServiceClient;
import iot.qiot.piros.edge.factory.model.SubscriptionRequest;
import iot.qiot.piros.edge.factory.model.SubscriptionResponse;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

@ApplicationScoped
public class FactoryService {

  private static final Logger LOG = LoggerFactory.getLogger(MachineryServiceApplication.class);

  private final FactoryServiceClient factoryClient;
  private final MachineryConfiguration machineryConfiguration;
  private final ObjectMapper objectMapper;

  public FactoryService(
      @RestClient FactoryServiceClient factoryClient,
      MachineryConfiguration machineryConfiguration) {
    this.factoryClient = factoryClient;
    this.machineryConfiguration = machineryConfiguration;

    this.objectMapper = new ObjectMapper();
  }

  public void subscribeMachinery() {
    LOG.info("qiot.machinery.factory - Subscribing machinery");
    Path subscriptionFile = Path.of(machineryConfiguration.subscriptionFile());
    if (Files.exists(subscriptionFile)) {
      LOG.info("qiot.machinery.factory - Machinery already subscribed");
    } else {
      SubscriptionRequest request = buildSubscriptionRequest();
      SubscriptionResponse response = factoryClient.subscribeMachinery(request);
      writeDataToDisk(response);
      LOG.info("qiot.machinery.factory - Finished subscribing new machinery with id {}",
          response.getId());
    }
  }

  private void writeDataToDisk(SubscriptionResponse response) {
    LOG.info("qiot.machinery.factory - Attempting to write data to disk");
    try {
      writeSubscriptionDataToDisk(response);
      writeKeystoreToDisk(response.getKeystore());
      writeTruststoreToDisk(response.getTruststore());
    } catch (IOException e) {
      LOG.error("qiot.machinery.factory - Error writing data to disk", e);
    }
  }

  private void writeSubscriptionDataToDisk(SubscriptionResponse response) throws IOException {
    Path subscriptionFile = Path.of(machineryConfiguration.subscriptionFile());
    Files.createDirectories(subscriptionFile.getParent());
    objectMapper.writeValue(subscriptionFile.toFile(), response);
    LOG.info("qiot.machinery.factory - Successfully stored subscription data: {}", response);
  }

  private void writeKeystoreToDisk(String keystoreBase64Encoded) throws IOException {
    Path keystorePath = Path.of(machineryConfiguration.keystoreFile());
    byte[] keystoreContent =
        Base64.getDecoder().decode(keystoreBase64Encoded.getBytes(StandardCharsets.UTF_8));
    Files.createDirectories(keystorePath.getParent());
    Files.write(keystorePath, keystoreContent);
    LOG.info("qiot.machinery.factory - Successfully stored keystore");
  }

  private void writeTruststoreToDisk(String trustStoreBase64Encoded) throws IOException {
    Path truststorePath = Path.of(machineryConfiguration.truststoreFile());
    byte[] truststoreContent =
        Base64.getDecoder().decode(trustStoreBase64Encoded.getBytes(StandardCharsets.UTF_8));
    Files.createDirectories(truststorePath.getParent());
    Files.write(truststorePath, truststoreContent);
    LOG.info("qiot.machinery.factory - Successfully stored truststore");
  }

  private SystemInformation getSystemInformation() {
    Map<String, Object> systemInformationMap = new HashMap<>();
    Yaml yaml = new Yaml();
    try (InputStream ios = new FileInputStream(machineryConfiguration.systemInformationPath())) {
      systemInformationMap = yaml.load(ios);
    } catch (Exception e) {
      LOG.error("qiot.machinery.factory - Error reading system information", e);
    }
    return SystemInformation.builder()
        .serialNumber((String) systemInformationMap.getOrDefault(
            "SerialNumber",
            UUID.randomUUID().toString()))
        .productName((String) systemInformationMap.getOrDefault(
            "ProductName",
            "default-machinery"))
        .build();
  }

  private SubscriptionRequest buildSubscriptionRequest() {
    SystemInformation systemInformation = getSystemInformation();
    return SubscriptionRequest.builder()
        .serial(systemInformation.getSerialNumber())
        .name(systemInformation.getProductName())
        .keyStorePassword(machineryConfiguration.keyStorePassword())
        .build();
  }
}
