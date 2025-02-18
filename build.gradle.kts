plugins {
    id("com.android.application") version "8.2.0" apply false
    id("org.jetbrains.kotlin.android") version "1.9.10" apply false
    id("com.google.gms.google-services") version "4.4.0" apply false
    id("org.jetbrains.kotlin.kapt") version "1.9.10" apply false


}

configurations {
    all {
        exclude("com.google.protobuf', module: 'protobuf-java")
    }
}


