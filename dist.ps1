$distPath = "target\dist"
$destPath = "C:\Users\kuba\Dropbox\WapUniverse"

New-Item "target\dist" -ItemType Directory -Force
Copy-Item -Path "target\WapUniverse.exe" -Destination $distPath 
Copy-Item -Path "target\WapUniverse-0.5-SNAPSHOT-jar-with-dependencies.jar" -Destination $distPath 
Copy-Item -Path $distPath -Recurse -Destination $destPath -Container -Force
