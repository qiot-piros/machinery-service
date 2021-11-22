package iot.qiot.piros.edge.configuration;

import io.smallrye.config.ConfigMapping;

@ConfigMapping(prefix = "machinery")
public interface MachineryConfiguration {

  String serial();

  String name();

  String keyStorePassword();

  String systemInformationPath();

  String dataFolder();
}
