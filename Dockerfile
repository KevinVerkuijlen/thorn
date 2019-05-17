FROM ubuntu:18.04

COPY ./target/demo-thorntail.jar /opt
RUN apt update -y \
	&& apt install -y openjdk-11-jre

CMD java -jar /opt/demo-thorntail.jar -Djava.net.preferIPv4Stack=true
