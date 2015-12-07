<?xml version="1.0" encoding="UTF-8"?>
<?eclipse version="3.0"?>

<plugin>

  <extension point="org.eclipse.emf.ecore.generated_package">
    <package 
       uri = "${uri}" 
       class = "${className}.${name}Package"
       genModel = "model/generated/${name}.genmodel" /> 
    
  </extension>



</plugin>