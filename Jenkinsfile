pipeline {
  agent any
  stages {
    stage('Git Checkout') {
      steps {
        git(url: 'https://github.com/gigaorder/instagram-presentation-prototype', branch: 'tinker-staging', changelog: true, poll: true)
      }
    }
  }
}