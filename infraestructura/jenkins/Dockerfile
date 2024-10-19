
FROM jenkins/jenkins:lts-jdk17
# cambiamos al usuario root para instalar los paquetes
USER root 

RUN apt-get update -y && apt-get install maven -y && rm -rf /var/lib/apt/lists/*
# es el usuario de Jenkins y tiene los permisos necesarios solo
USER jenkins 


#ver que cosas faltan instalar de extensiones cuando lo corramos
