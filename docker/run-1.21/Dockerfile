FROM knokkogator/kci:0.1.4-mc1.21 AS build_base
RUN mkdir /custom-items
WORKDIR /custom-items
COPY gradlew ./gradlew
RUN dos2unix ./gradlew
COPY gradle ./gradle
COPY kci-nms ./kci-nms

FROM build_base AS build_plugin
COPY bit-helper ./bit-helper
COPY shared-code ./shared-code
COPY plug-in ./plug-in
COPY custom-recipes ./custom-recipes
COPY .github/plug-in/build.gradle ./
COPY .github/plug-in/settings.gradle ./
RUN ./gradlew shadowJar --no-daemon

FROM build_base AS build_nms
RUN apk add zip
COPY kci-nms13plus ./kci-nms13plus
COPY kci-nms16plus ./kci-nms16plus
COPY kci-nms18plus ./kci-nms18plus
COPY kci-nms20plus ./kci-nms20plus
COPY kci-nms21 ./kci-nms21
COPY .github/mc1.21/build.gradle ./
COPY .github/mc1.21/settings.gradle ./
RUN ./gradlew build --no-daemon
RUN mkdir combined
RUN unzip -d combined -o kci-nms13plus/build/libs/kci-nms13plus.jar
RUN unzip -d combined -o kci-nms16plus/build/libs/kci-nms16plus.jar
RUN unzip -d combined -o kci-nms18plus/build/libs/kci-nms18plus.jar
RUN unzip -d combined -o kci-nms20plus/build/libs/kci-nms20plus.jar
RUN unzip -d combined -o kci-nms21/build/libs/kci-nms21.jar
COPY --from=build_plugin /custom-items/plug-in/build/libs/plug-in-all.jar ./plug-in-raw.jar
RUN unzip -d combined -o plug-in-raw.jar
WORKDIR /custom-items/combined
RUN zip -r CustomItems.jar *

FROM knokkogator/kci:0.1.4-mc1.21 AS prepared

RUN mkdir /server
RUN cp /build-tools/spigot-1.21.5.jar /server/server.jar
WORKDIR /server

RUN mkdir plugins
RUN echo eula=true > eula.txt
EXPOSE 25565

FROM prepared AS generated
RUN echo stop | java -jar server.jar
COPY --from=build_nms /custom-items/combined/CustomItems.jar ./plugins/CustomItems.jar
