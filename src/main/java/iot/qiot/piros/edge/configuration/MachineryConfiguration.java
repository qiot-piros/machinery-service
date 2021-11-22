package iot.qiot.piros.edge.configuration;

import io.smallrye.config.ConfigMapping;

@ConfigMapping(prefix = "machinery")
public interface MachineryConfiguration {

  String keyStorePassword();

  String systemInformationPath();

  String dataFolder();

  String subscriptionFile();

  String keystoreFile();

  String truststoreFile();
}
