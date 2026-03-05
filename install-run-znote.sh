#!/bin/sh
cd ~
rm -rf udav_project
#GetRepo
git clone git@github.com:mostali/udav_project.git

cd udav_project

#Create App Jar
mvnd -f mp/pom.xml clean package -Pzznote

#copy app.jar to any app-dir located in home, e.g. ~/appdir (home location is required)
mv zznote/target/app.jar .

#Unpack application.properties & run-script r.sh to app-dir
java -jar app.jar --init

chmod +x ./r.sh

#Choice way to run application
#1. Directly (via script r.sh or as java -jar app.jar )
#2. via Docker . Additionally, Docker container will be run as Desktop App if comment line which run application (see Dockerfile)

#1
# This script run application on port 80
#./r.sh --p
# or
# java -jar app.jar

#2
# Run in docker
mv zznote/Rocky.Dockerfile .
#sudo docker build -t cr-image:1 -f Rocky.Dockerfile . && sudo docker run -it -v /home/dav/.data/zzn:/home/lu/.data/zzn -e VNC_PW=password -p 8081:8081 cr-image:1
sudo docker build -t cr-image:1 -f Rocky.Dockerfile . && sudo docker run -it -e VNC_PW=password -p 8081:8081 cr-image:1