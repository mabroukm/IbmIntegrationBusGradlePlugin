package org.mostafa.plugin.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.api.GradleException
import org.gradle.process.ExecResult
import groovy.util.XmlSlurper

class PackTask extends DefaultTask {

	PackTask() {        
        group = 'IIB'	
    }
	
    @TaskAction
    def pack() {
		 println 'Building TestApp project and creating BAR file'
		def stdOut = new ByteArrayOutputStream()
		def errOut = new ByteArrayOutputStream()
        def apps = ''
		if(project.iib.applications!=null){					
			apps = project.iib.applications
			println 'Only the following projects will be included:' + apps
		}else{
			println "projects namelist couldn't be loaded from the build.properties file. All projects will be included"
			def rDir = new File('../')
			rDir.traverse( maxDepth: 1, type: groovy.io.FileType.DIRECTORIES){
			def pfile = new File('../'+it.name+'/.project')
			if(pfile.exists()){
				def response = new XmlSlurper().parseText(pfile.text)
				def pType = response.natures.nature[0]  
				if(pType=='com.ibm.etools.msgbroker.tooling.applicationNature'){
					apps = (apps==''?'':' ') + it.name
					println 'App '+it.name+' will be included in the bar'
				}
			}
			}
		}
		println 'The applications which will be included in the bar are:'+apps
		def ExecResult result = project.exec{
			commandLine 'cmd', '/c', 'mqsicreatebar', '-data', '../' , '-b', 'AllApps.bar', '-cleanBuild', '-a', apps
			standardOutput = stdOut
			errorOutput = errOut
		}
		if(result.exitValue!=0){
			println errOut.toString()
			throw new GradleException('mqsicreatebar failed');
		}
		println stdOut.toString()
		println 'Task Completed'
	}
}