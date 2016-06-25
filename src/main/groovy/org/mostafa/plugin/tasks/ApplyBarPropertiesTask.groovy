package org.mostafa.plugin.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.api.GradleException
import org.gradle.process.ExecResult
import org.gradle.api.GradleException

class ApplyBarPropertiesTask extends DefaultTask {
	ApplyBarPropertiesTask() {        
        group = 'IIB'
    }
    @TaskAction
    def apply() {
		println 'Applying bar properties'
		if(project.iib.barPropertiesFile==null || project.iib.barPropertiesFile==''){
			return
		}
		def pfile = new File(project.iib.barPropertiesFile)
		if(!pfile.exists()){
			println 'properties file ('+project.iib.barPropertiesFile+') does not exist'
			throw new GradleException('mqsiapplybaroverride failed');
		}
		def stdOut = new ByteArrayOutputStream()
		def errOut = new ByteArrayOutputStream()
        def apps = ''
		def ExecResult result = project.exec{
			commandLine 'cmd', '/c', 'mqsiapplybaroverride', '-b', 'AllApps.bar', '-r', '-p', project.iib.barPropertiesFile , '-o', 'AllApps.bar'
			standardOutput = stdOut
			errorOutput = errOut
		}
		if(result.exitValue!=0){
			println errOut.toString()
			throw new GradleException('mqsiapplybaroverride failed');
		}
		println stdOut.toString()
		println 'Task Completed'
	}
}