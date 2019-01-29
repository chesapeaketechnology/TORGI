FROM java:8-jdk

RUN apt-get update
RUN apt-get install -y wget git unzip

RUN mkdir -p /android

WORKDIR /android

#ENV ANDROID_SDK_VERSION=r26.1.1
#
#RUN wget -q https://dl.google.com/android/repository/tools_${ANDROID_SDK_VERSION}-linux.zip \
# && unzip -q tools_${ANDROID_SDK_VERSION}-linux.zip \
# && rm tools_${ANDROID_SDK_VERSION}-linux.zip

ENV sdk_version=sdk-tools-linux-3859397.zip

RUN wget -q https://dl.google.com/android/repository/${sdk_version} \
 && unzip -q ${sdk_version} \
 && rm ${sdk_version}

#ENV ANDROID_NDK_VERSION r18b
#
#RUN wget -q https://dl.google.com/android/repository/android-ndk-${ANDROID_NDK_VERSION}-linux-x86_64.zip \
# && unzip -q android-ndk-${ANDROID_NDK_VERSION}-linux-x86_64.zip \
# && rm android-ndk-${ANDROID_NDK_VERSION}-linux-x86_64.zip

ENV ANDROID_HOME=/android
#ENV ANDROID_NDK_HOME=/android/android-ndk-${ANDROID_NDK_VERSION}
ENV PATH=${ANDROID_HOME}/tools:${ANDROID_HOME}/tools/bin:${ANDROID_HOME}/platform-tools:${ANDROID_NDK_HOME}:$PATH

RUN mkdir -p ${ANDROID_HOME}/licenses \
 && touch ${ANDROID_HOME}/licenses/android-sdk-license \
 && echo "\n8933bad161af4178b1185d1a37fbf41ea5269c55" >> $ANDROID_HOME/licenses/android-sdk-license \
 && echo "\nd56f5187479451eabf01fb78af6dfcb131a6481e" >> $ANDROID_HOME/licenses/android-sdk-license \
 && echo "\ne6b7c2ab7fa2298c15165e9583d0acf0b04a2232" >> $ANDROID_HOME/licenses/android-sdk-license \
 && echo "\n84831b9409646a918e30573bab4c9c91346d8abd" > $ANDROID_HOME/licenses/android-sdk-preview-license \
 && echo "\nd975f751698a77b662f1254ddbeed3901e976f5a" > $ANDROID_HOME/licenses/intel-android-extra-license

RUN yes | sdkmanager --licenses
RUN yes | sdkmanager "platforms;android-28"
RUN mkdir -p ${ANDROID_HOME}/.android \
 && touch ~/.android/repositories.cfg ${ANDROID_HOME}/.android/repositories.cfg
RUN yes | sdkmanager "build-tools;28.0.3"
RUN yes | sdkmanager "extras;android;m2repository"

RUN DEBIAN_FRONTEND=noninteractive apt-get install -y locales
RUN sed -i -e 's/# en_US.UTF-8 UTF-8/en_US.UTF-8 UTF-8/' /etc/locale.gen && \
    DEBIAN_FRONTEND=noninteractive dpkg-reconfigure --frontend=noninteractive locales && \
    update-locale LANG=en_US.UTF-8

ENV LANG=en_US.UTF-8 LC_ALL=en_US.UTF-8

#RUN git clone https://github.com/ngageoint/geopackage-android /geopackage-android
#WORKDIR /geopackage-android
#RUN sed -i -e 's/^android {/android {\n lintOptions {\n    abortOnError false\n  }/' geopackage-sdk/build.gradle
#RUN ./gradlew build

WORKDIR /torgi

COPY . .
#RUN ln -nsf /geopackage-android/geopackage-sdk geopackage-sdk
RUN sed -i -e 's/^android {/android {\n lintOptions {\n    abortOnError false\n  }/' torgi/build.gradle
RUN ./gradlew build

CMD sleep 3600
