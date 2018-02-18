node {
    def app
   stage('Clone') { // for display purposes
      // Get some code from a GitHub repository
      git 'https://github.com/BrokenFire/BrokenDiscordBot.git'
   }
   stage('Build image') {
        /* This builds the actual image; synonymous to
         * docker build on the command line */

        app = docker.build("brokenfire/testjenkins","--rm=true .")
    }
   stage('Push image') {
        /* Finally, we'll push the image with two tags:
         * First, the incremental build number from Jenkins
         * Second, the 'latest' tag.
         * Pushing multiple tags is cheap, as all the layers are reused. */
        // app.push("${env.BUILD_NUMBER}")
        // app.push("latest")
        app.push()
        app.push("${env.BUILD_NUMBER}")
        
    }
    stage('Cleaning'){
        sh "docker image prune -f"
    }
} 