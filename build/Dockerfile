FROM frolvlad/alpine-oraclejdk8:slim
MAINTAINER Andras Gazdag "agazdag@precognox.com"
RUN apk update && apk add bash
ADD distributions/TLOG16RS-0.1.0-SNAPSHOT.tar /tmp
RUN mv /tmp/TLOG16RS-0.1.0-SNAPSHOT /TLOG16RS
WORKDIR /
CMD ["/TLOG16RS/bin/TLOG16RS", "server", "TLOG16RS/config.yml"]
