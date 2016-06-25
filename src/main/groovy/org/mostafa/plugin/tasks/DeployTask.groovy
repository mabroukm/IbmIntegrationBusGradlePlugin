package org.mostafa.plugin.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.api.GradleException
import org.gradle.process.ExecResult
import org.gradle.api.GradleException

class DeployTask extends DefaultTask {
	DeployTask() {        
        group = 'IIB'
    }
    @TaskAction
    def deploy() {
		def stdOut = new ByteArrayOutputStream()
		def errOut = new ByteArrayOutputStream()
		if(project.iib.brokerFile!=null){
			println 'Deploying bar to brokerFile:'+project.iib.brokerFile
			def ExecResult result = project.exec{
			commandLine 'cmd', '/c', 'mqsideploy', '-n', project.iib.brokerFile, '-a', 'AllApps.bar', '-e', project.iib.executionGroup
			standardOutput = stdOut
			errorOutput = errOut
			}
			if(result.exitValue!=0){
				println errOut.toString()
				throw new GradleException('mqsiapplybaroverride failed');
			}
		}
		if(project.iib.localBroker!=null){
			println 'Deploying bar to localBroker:'+project.iib.localBroker
			stdOut.reset()
			errOut.reset()
			def ExecResult result = project.exec{
			commandLine 'cmd', '/c', 'mqsideploy', project.iib.localBroker,'-a', 'AllApps.bar', '-e', project.iib.executionGroup
			standardOutput = stdOut
			errorOutput = errOut
			}
			if(result.exitValue!=0){
				println errOut.toString()
				throw new GradleException('mqsiapplybaroverride failed');
			}
		}		
		println stdOut.toString()
		println 'Task Completed'
	}
}