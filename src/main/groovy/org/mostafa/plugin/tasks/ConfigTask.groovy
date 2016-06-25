package org.mostafa.plugin.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecResult

class ConfigTask extends DefaultTask {
	
	ConfigTask() {        
        group = 'IIB'
    }
    @TaskAction
    def config() {
		if(project.iib.brokerFile==null && project.iib.localBroker==null){
			println 'Neither brokerFile nor localBroker is set. Checking if the system has any local brokers'
			def stdOut = new ByteArrayOutputStream()
			def ExecResult result = project.exec{ 
										commandLine 'cmd', '/c', 'mqsilist'
										standardOutput = stdOut
									}
			if(result.exitValue==0){
				String response = stdOut.toString()
				def splitedStr = response.split("'")
				if(splitedStr.length < 2){
					println 'Failed to configure a broker object. Deployment and test tasks will not be available'
					return
				}
				def localBroker = splitedStr[1];
				project.iib.localBroker = localBroker
				//Getting the execution group
				stdOut.reset()
				result = project.exec{ 
										commandLine 'cmd', '/c', 'mqsilist', localBroker
										standardOutput = stdOut
									}
				if(result.exitValue==0){
					response = stdOut.toString()
					if(response.contains("Execution group 'default'")){					
						project.iib.executionGroup = 'default';
						println 'The deployment and test tasks will work on localBroker (' + localBroker +') and execution group (default)'
						return
					}
					splitedStr = response.split("'")
					if(splitedStr.length < 2){
						println 'Failed to configure a broker object. Deployment and test tasks will not be available'
						return
					}
					def eg = splitedStr[1];
					project.iib.executionGroup = eg
					println 'The deployment and test tasks will work on localBroker (' + localBroker +') and execution group ('+eg+')'
				}else{
					println 'Failed to configure a broker object. Deployment and test tasks will not be available'
				}
			}else{
				println 'Failed to configure a broker object. Deployment and test tasks will not be available'
			}
		}
    }
}