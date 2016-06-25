package org.mostafa.plugin

import org.gradle.api.Project
import org.gradle.api.Plugin
import org.mostafa.plugin.tasks.PackTask
import org.mostafa.plugin.tasks.ConfigTask
import org.mostafa.plugin.tasks.ApplyBarPropertiesTask
import org.mostafa.plugin.tasks.DeployTask

class IbmIntegrationBusPlugin implements Plugin<Project> {
    void apply(Project project) {
         project.extensions.create('iib', IbmIntegrationBusPluginExtension)
		 project.task('pack',type: PackTask)		 
		 project.task('applyproperties', type: ApplyBarPropertiesTask)
		 project.task('config', type: ConfigTask)
		 project.task('deploy', type: DeployTask)
		 project.applyproperties.dependsOn(project.pack)
		 project.deploy.dependsOn(project.applyproperties)
		 project.deploy.dependsOn(project.config)
		 
		 project.applyproperties.onlyIf{
			if(project.iib.barPropertiesFile==null){
				println 'iib.barPropertiesFile property is not set hence skipping the applyproperties task'
				false
			}
			true
		 }
		 project.config.onlyIf{
			(project.iib.brokerFile==null && project.iib.localBroker==null) || project.iib.executionGroup==null
		 }
		
		 project.deploy.onlyIf{
			(project.iib.brokerFile!=null || project.iib.localBroker!=null) && project.iib.executionGroup!=null
		 }
		 
		 
		 //setting the input and output dirs
		 project.afterEvaluate{
			if(project.iib.srcDirs!=null){
				println 'Considering the build source dirs: '+project.iib.srcDirs
				project.pack.outputs.file('file:AllApps.bar')
				project.iib.srcDirs.split(" ").each{
				def sDir = new File('../'+it)
					if(sDir.exists()){
						project.pack.inputs.dir(sDir)
					}
				}
			}
		}
		 
    }
}