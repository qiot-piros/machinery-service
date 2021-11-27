package iot.qiot.piros.edge.facility;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import iot.qiot.piros.edge.MachineryServiceApplication;
import iot.qiot.piros.edge.configuration.MachineryConfiguration;
import iot.qiot.piros.edge.core.model.SystemInformation;
import iot.qiot.piros.edge.facility.client.FacilityServiceClient;
import iot.qiot.piros.edge.facility.model.Machinery;
import iot.qiot.piros.edge.facility.model.SubscriptionRequest;
import iot.qiot.piros.edge.facility.model.SubscriptionResponse;
import iot.qiot.piros.edge.service.FacilityService;
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
public class FacilityServiceImpl implements FacilityService {

  private static final Logger LOG = LoggerFactory.getLogger(MachineryServiceApplication.class);

  private final FacilityServiceClient facilityClient;
  private final MachineryConfiguration machineryConfiguration;
  private final ObjectMapper objectMapper;

  private Machinery machinery;

  public FacilityServiceImpl(
      @RestClient FacilityServiceClient facilityClient,
      MachineryConfiguration machineryConfiguration) {
    this.facilityClient = facilityClient;
    this.machineryConfiguration = machineryConfiguration;

    this.objectMapper = new ObjectMapper();
    this.objectMapper.registerModule(new JavaTimeModule());
  }

  @Override
  public void subscribeMachinery() {
    LOG.info("qiot.machinery.facility - Subscribing machinery");
    Path subscriptionFile = Path.of(machineryConfiguration.subscriptionFile());
    if (Files.exists(subscriptionFile)) {
      LOG.info("qiot.machinery.facility - Machinery already subscribed");
    } else {
      SubscriptionRequest request = buildSubscriptionRequest();
      SubscriptionResponse response = facilityClient.subscribeMachinery(request);
      writeDataToDisk(response);
      String id = response != null ? response.getId().toString() : "undefined";
      this.machinery = new Machinery(id);
      LOG.info("qiot.machinery.facility - Finished subscribing new machinery with id {}", id);
    }
  }

  @Override
  public String getMachineId() {
    return machinery.getId();
  }

  private void writeDataToDisk(SubscriptionResponse response) {
    LOG.info("qiot.machinery.facility - Attempting to write data to disk");
    try {
      writeSubscriptionDataToDisk(response);
      writeKeystoreToDisk(response.getKeystore());
      writeTruststoreToDisk(response.getTruststore());
    } catch (Exception e) {
      LOG.error("qiot.machinery.facility - Error writing data to disk", e);
    }
  }

  private void writeSubscriptionDataToDisk(SubscriptionResponse response) throws IOException {
    Path subscriptionFile = Path.of(machineryConfiguration.subscriptionFile());
    Files.createDirectories(subscriptionFile.getParent());
    objectMapper.writeValue(subscriptionFile.toFile(), response);
    LOG.info("qiot.machinery.facility - Successfully stored subscription data");
  }

  private void writeKeystoreToDisk(String keystoreBase64Encoded) throws IOException {
    Path keystorePath = Path.of(machineryConfiguration.keystoreFile());
    byte[] keystoreContent =
        Base64.getDecoder().decode(keystoreBase64Encoded.getBytes(StandardCharsets.UTF_8));
    Files.createDirectories(keystorePath.getParent());
    Files.write(keystorePath, keystoreContent);
    LOG.info("qiot.machinery.facility - Successfully stored keystore");
  }

  private void writeTruststoreToDisk(String trustStoreBase64Encoded) throws IOException {
    Path truststorePath = Path.of(machineryConfiguration.truststoreFile());
    byte[] truststoreContent =
        Base64.getDecoder().decode(trustStoreBase64Encoded.getBytes(StandardCharsets.UTF_8));
    Files.createDirectories(truststorePath.getParent());
    Files.write(truststorePath, truststoreContent);
    LOG.info("qiot.machinery.facility - Successfully stored truststore");
  }

  private SystemInformation getSystemInformation() {
    Map<String, Object> systemInformationMap = new HashMap<>();
    Yaml yaml = new Yaml();
    try (InputStream ios = new FileInputStream(machineryConfiguration.systemInformationPath())) {
      systemInformationMap = yaml.load(ios);
    } catch (Exception e) {
      LOG.error("qiot.machinery.facility - Error reading system information", e);
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
