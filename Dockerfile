FROM ubuntu:18.04
RUN ls -a
COPY ./target/thorntail-thorntail.jar /opt
RUN apt update -y \
	&& apt install -y openjdk-11-jre

CMD java -jar /opt/thorntail-thorntail.jar -Djava.net.preferIPv4Stack=true
