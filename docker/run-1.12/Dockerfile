FROM knokkogator/kci:0.1.2-mc1.12 AS build_base
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
COPY kci-nms12 ./kci-nms12
COPY .github/mc1.12/build.gradle ./
COPY .github/mc1.12/settings.gradle ./
RUN ./gradlew build --no-daemon
RUN mkdir combined
RUN unzip -d combined kci-nms12/build/libs/kci-nms12.jar
COPY --from=build_plugin /custom-items/plug-in/build/libs/plug-in-all.jar ./plug-in-raw.jar
RUN unzip -d combined -o plug-in-raw.jar
WORKDIR /custom-items/combined
RUN zip -r CustomItems.jar *

FROM knokkogator/kci:0.1.2-mc1.12 AS prepared

RUN mkdir /server
RUN cp /build-tools/craftbukkit-1.12.2.jar /server/server.jar
WORKDIR /server

RUN mkdir plugins
RUN echo eula=true > eula.txt
EXPOSE 25565

FROM prepared AS generated
RUN echo stop | java -jar server.jar
COPY --from=build_nms /custom-items/combined/CustomItems.jar ./plugins/CustomItems.jar
