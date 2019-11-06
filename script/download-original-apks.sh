#!/bin/bash
source ./ssh.cfg
rm -rf /var/jenkins_home/files/feed2wall/apk
mkdir -p /var/jenkins_home/files/feed2wall/apk
sshpass -p ${pwd} scp -r ${usr}@${domain}:/home/dev/tinker-server/archive/instagramPatching/* /var/jenkins_home/files/feed2wall/apk/
echo "Download finished"