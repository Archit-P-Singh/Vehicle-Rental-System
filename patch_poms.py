import xml.etree.ElementTree as ET
import os

ET.register_namespace('', 'http://maven.apache.org/POM/4.0.0')

def add_module_to_parent():
    tree = ET.parse('pom.xml')
    root = tree.getroot()
    ns = {'mvn': 'http://maven.apache.org/POM/4.0.0'}
    modules = root.find('mvn:modules', ns)
    if modules is not None:
        exists = False
        for m in modules.findall('mvn:module', ns):
            if m.text == 'common-dto':
                exists = True
                break
        if not exists:
            elem = ET.Element('{http://maven.apache.org/POM/4.0.0}module')
            elem.text = 'common-dto'
            modules.insert(0, elem)
            tree.write('pom.xml', xml_declaration=True, encoding='UTF-8')

def add_deps_to_service(service):
    pom_path = f'{service}/pom.xml'
    if not os.path.exists(pom_path):
        return
    tree = ET.parse(pom_path)
    root = tree.getroot()
    ns = {'mvn': 'http://maven.apache.org/POM/4.0.0'}
    
    deps = root.find('mvn:dependencies', ns)
    if deps is None:
        deps = ET.Element('{http://maven.apache.org/POM/4.0.0}dependencies')
        root.append(deps)
    
    # Check if common-dto exists
    common_dto_exists = False
    kafka_exists = False
    
    for d in deps.findall('mvn:dependency', ns):
        art = d.find('mvn:artifactId', ns)
        if art is not None and art.text == 'common-dto':
            common_dto_exists = True
        if art is not None and art.text == 'spring-kafka':
            kafka_exists = True
            
    if not common_dto_exists:
        dep = ET.Element('{http://maven.apache.org/POM/4.0.0}dependency')
        g = ET.Element('{http://maven.apache.org/POM/4.0.0}groupId')
        g.text = 'com.fleetflow'
        a = ET.Element('{http://maven.apache.org/POM/4.0.0}artifactId')
        a.text = 'common-dto'
        v = ET.Element('{http://maven.apache.org/POM/4.0.0}version')
        v.text = '${project.version}'
        dep.append(g)
        dep.append(a)
        dep.append(v)
        deps.append(dep)
        
    if not kafka_exists:
        dep = ET.Element('{http://maven.apache.org/POM/4.0.0}dependency')
        g = ET.Element('{http://maven.apache.org/POM/4.0.0}groupId')
        g.text = 'org.springframework.kafka'
        a = ET.Element('{http://maven.apache.org/POM/4.0.0}artifactId')
        a.text = 'spring-kafka'
        dep.append(g)
        dep.append(a)
        deps.append(dep)

    tree.write(pom_path, xml_declaration=True, encoding='UTF-8')

add_module_to_parent()
for srv in ['vehicle-service', 'booking-service', 'pricing-service', 'payment-service']:
    add_deps_to_service(srv)
    
print("POMs patched successfully")
