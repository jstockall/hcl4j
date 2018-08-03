HCL4j
=====

HCL4j is a Parser for the Hashicorp Configuration Language on the JVM. This provides a mechanism for converting HCL syntax into an a HCL configuration object that can be exported to map or string for further inspection. 

Features:

* Support for Syntax parsing
* Nested Array and Map support


## Installation

### Three repositories
* orgin master https://github.com/bertramdev/hcl4j (original)
* orgin upstream https://github.com/jstockall/hcl4j (forked so it can be imported to GitLab internal)
* internal master http://svrrepo.embotics.com/development/hcl4j (active development)

### Ivy Publishing
 * This published to com.embotics/hcl4j/$revision for some reason it needs to be manually moved in Artifactory
 * Ivy / Artifactory config in build.gradle

Using gradle one can include the hcl4j dependency like so:

```groovy
dependencies {
	compile "com.bertramlabs.plugins:hcl4j:0.1.1"
}
```

Using Ivy one can include the hcl4j dependency like so:
```<dependency org="com.embotics" name="hcl4j" rev="0.1.9">
    <artifact name="hcl4j" ext="jar"/>
</dependency>
```

## Usage

Using the HCL Parser is fairly straightfoward. Most calls are still limited to use of the `HCLParser` class itself. There are several `parse` method helpers supporting both `File`, `InputStream`, `String`, and `Reader` as inputs.


```java
import com.bertramlabs.plugins.hcl4j.HCLParser;

File terraformFile = new File("terraform.tf");

HCLParser p = new HCLParser();
HCLObject configuration = p.parse(terraformFile);

Map results = new HCL2Map().toMap(configuration);

or

String result  = new HCL2String().toMap(configuration);
```

For More Information on the HCL Syntax Please see the project page:

[https://github.com/hashicorp/hcl](https://github.com/hashicorp/hcl)


## Things to be Done

This plugin does not yet handle processing of the interpolated string syntax. While it does generate it into the result map, Parsing the values of the interpolation syntax needs to be done in a follow up step using some type of HCL runtime engine
