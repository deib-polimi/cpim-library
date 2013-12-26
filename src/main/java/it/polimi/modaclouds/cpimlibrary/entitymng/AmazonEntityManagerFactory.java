package it.polimi.modaclouds.cpimlibrary.entitymng;

/*
 * *****************************
 * cpim-library
 * *****************************
 * Copyright (C) 2013 deib-polimi
 * *****************************
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * *****************************
 */


import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

import com.spaceprogram.simplejpa.EntityManagerFactoryImpl;

public class AmazonEntityManagerFactory extends CloudEntityManagerFactory {
	
	private static EntityManagerFactoryImpl factory;
	//private static EntityManagerFactory factory;
	private String persistenceUnit;
	private String packageName;
	
	public AmazonEntityManagerFactory(String persistenceUnit, String packageName){
		this.persistenceUnit= persistenceUnit;
		this.packageName = packageName;
		//this.packageName = "com.appcloud.mic.entity";
		
		
		Set<String> classNames = null;
		try {
			classNames = this.getClasses();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		classNames.add("it.polimi.modaclouds.cpimlibrary.diagnostics.Diagnostics");
		
		
		/*
		//itero per ogni entity
		Iterator<String> iter = classNames.iterator();
		while(iter.hasNext()) {
			System.out.println(iter.next().toString());
			Class<?> entity = null;
			try {
				entity = Class.forName(iter.next().toString());
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			//System.out.println(entity.getMethods().length);
			//for(int i = 0; i < entity.getMethods().length; i++)
			//	System.out.println(entity.getMethods()[i].getName());
			
			//itero per ogni attributo
			Field [] fields = entity.getDeclaredFields();
			for(int i = 0; i < fields.length; i ++) {
				
				
				System.out.println("Attributo " + fields[i].getName() + " di tipo: " + fields[i].getType().getName());
				if(fields[i].getType().getName().equals("java.util.ArrayList")) {
					
					fields[i].setAccessible(true);
					
					System.out.println(fields[i].getDeclaringClass().getName());
					System.out.println(fields[i].getName());
					System.out.println(fields[i].getGenericType());
					System.out.println(fields[i].getModifiers());
					System.out.println(fields[i].getAnnotations().length);
					
				}
				
				
				//itero per ogni annotazione
				Annotation [] annotations = fields[i].getAnnotations();
				for(int j = 0; j < annotations.length; j++) {
					String a = annotations[j].toString();
					//cambio l'annotazione embedded
					if(a.equals("@javax.persistence.Embedded()")) a = "@com.spaceprogram.simplejpa.MultiValueProperty";
				}
			}
			
			
			//itero per ogni metodo
			Method[] methods = entity.getDeclaredMethods();
			for(int i = 0; i < methods.length; i ++) {
				
				System.out.println("Metodo " + methods[i].getName() + " di tipo: " + methods[i].getReturnType().getName());
				
			}
			
			
		}
		*/
		
		factory = new EntityManagerFactoryImpl(persistenceUnit, null, null, classNames);
		//factory = Persistence.createEntityManagerFactory(persistenceUnit);
		System.out.println("factory " + factory);
	}
	
	@Override
	public CloudEntityManager createCloudEntityManager() {
		return new AmazonEntityManager(factory.createEntityManager(), packageName);
	}

	@Override
	public void close() {
		if(persistenceUnit != null){
			super.removeMF(persistenceUnit);
			factory.close();
		}
	}
	
	private Set<String> getClasses() throws Exception {
		   java.lang.ClassLoader loader = Thread.currentThread().getContextClassLoader();
		   return getClasses(loader);
	}

	private Set<String> getClasses(java.lang.ClassLoader loader){
	   Set<String> classes = new HashSet<String>();
	   String path = packageName.replace('.', '/');
	   Enumeration<URL> resources = null;
	   try {
		   resources = loader.getResources(path);
	   } catch (IOException e) {
		   // TODO Auto-generated catch block
		   e.printStackTrace();
	   }
	   if (resources != null) {
		   while (resources.hasMoreElements()) {
			   String filePath = resources.nextElement().getFile();
			   if(filePath.indexOf("%20") > 0)
				   filePath = filePath.replaceAll("%20", " ");
			   if (filePath != null) {
				   classes.addAll(getFromDirectory(new File(filePath)));
			   }
		   }
	   }
	   return classes;
	}
		 
	private Set<String> getFromDirectory(File directory) {
		Set<String> classes = new HashSet<String>();
		if (directory.exists()) {
			for (String file : directory.list()) {
				if (file.endsWith(".class")) {
					String name = packageName + '.' + stripFilenameExtension(file);
					//System.out.println("Classes retrieved: " + name + ".");
					classes.add(name);
				}
			}
		}
		return classes;
	}

	private String stripFilenameExtension(String file) {
		file = file.substring(0, file.indexOf(".class"));
		return file;
	}


}
