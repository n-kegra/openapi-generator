package org.openapitools.codegen.languages;

import org.openapitools.codegen.*;
import org.openapitools.codegen.utils.ModelUtils;

import io.swagger.models.properties.ArrayProperty;
import io.swagger.models.properties.MapProperty;
import io.swagger.models.properties.Property;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.models.parameters.Parameter;

import java.io.File;
import java.util.*;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CppDrogonClientCodegen extends DefaultCodegen implements CodegenConfig {
    public static final String PROJECT_NAME = "projectName";

    static final Logger LOGGER = LoggerFactory.getLogger(CppDrogonClientCodegen.class);    
    protected static final String CPP_NAMESPACE = "cppNamespace";
    protected static final String CPP_NAMESPACE_DESC = "C++ namespace (convention: name::space::for::api).";
    protected String cppNamespace = "OpenAPI";

    protected String cmakeTarget = "OpenAPI";
    protected String includeDir = "OpenAPI";
    protected String prefix = "OAPI";

    protected Set<String> commonIncludes = new HashSet<>();
    protected Map<String, String> importMapping = new HashMap<>();

    public CodegenType getTag() {
        return CodegenType.CLIENT;
    }

    public String getName() {
        return "cpp-drogon";
    }

    public String getHelp() {
        return "Generates a cpp-drogon client.";
    }

    public CppDrogonClientCodegen() {
        super();

        reservedWords.addAll(
            Arrays.asList(
                    "alignas",
                    "alignof",
                    "and",
                    "and_eq",
                    "asm",
                    "auto",
                    "bitand",
                    "bitor",
                    "bool",
                    "break",
                    "case",
                    "catch",
                    "char",
                    "char16_t",
                    "char32_t",
                    "class",
                    "compl",
                    "concept",
                    "const",
                    "constexpr",
                    "const_cast",
                    "continue",
                    "decltype",
                    "default",
                    "delete",
                    "do",
                    "double",
                    "dynamic_cast",
                    "else",
                    "enum",
                    "explicit",
                    "export",
                    "extern",
                    "false",
                    "float",
                    "for",
                    "friend",
                    "goto",
                    "if",
                    "inline",
                    "int",
                    "linux",
                    "long",
                    "mutable",
                    "namespace",
                    "new",
                    "noexcept",
                    "not",
                    "not_eq",
                    "nullptr",
                    "operator",
                    "or",
                    "or_eq",
                    "private",
                    "protected",
                    "public",
                    "register",
                    "reinterpret_cast",
                    "requires",
                    "return",
                    "short",
                    "signed",
                    "sizeof",
                    "static",
                    "static_assert",
                    "static_cast",
                    "struct",
                    "switch",
                    "template",
                    "this",
                    "thread_local",
                    "throw",
                    "true",
                    "try",
                    "typedef",
                    "typeid",
                    "typename",
                    "union",
                    "unsigned",
                    "using",
                    "virtual",
                    "void",
                    "volatile",
                    "wchar_t",
                    "while",
                    "xor",
                    "xor_eq")
        );

        super.typeMapping = new HashMap<>();
        typeMapping.put("string", "std::string");
        typeMapping.put("integer", "int32_t");
        typeMapping.put("long", "int64_t");
        typeMapping.put("boolean", "bool");
        typeMapping.put("number", "double");
        typeMapping.put("array", "std::vector");
        typeMapping.put("map", "std::map");
        typeMapping.put("set", "std::set");

        typeMapping.put("UUID", "std::string");
        typeMapping.put("URI", "std::string");
        typeMapping.put("file", "drogon::UploadFile");
        typeMapping.put("binary", "std::basic_string_view<byte>");
        typeMapping.put("DateTime", "std::string");

        commonIncludes.add("int32_t");
        commonIncludes.add("int64_t");
        commonIncludes.add("bool");
        commonIncludes.add("double");
        commonIncludes.add("std::string");
        commonIncludes.add("drogon::UploadFile");

        importMapping.put("std::vector", "vector");
        importMapping.put("std::map", "map");
        importMapping.put("std::set", "set");

        addOption(CPP_NAMESPACE, CPP_NAMESPACE_DESC, this.cppNamespace);
        additionalProperties.put("cppNamespace", cppNamespace);
        addOption("cmakeTarget", "cmake target name", this.cmakeTarget);
        additionalProperties.put("cmakeTarget", cmakeTarget);
        addOption("includeDir", "include dir name", this.includeDir);
        additionalProperties.put("includeDir", includeDir);
        addOption("prefix", "header symbol prefix", this.prefix);
        additionalProperties.put("prefix", prefix);

        outputFolder = "generated-code" + File.separator + "cpp-drogon";
        modelTemplateFiles.put("model-header.mustache", ".h");
        modelTemplateFiles.put("model-source.mustache", ".cpp");
        apiTemplateFiles.put("api-header.mustache", ".h");
        apiTemplateFiles.put("api-source.mustache", ".cpp");
        embeddedTemplateDir = templateDir = "cpp-drogon";
    }

    @Override
    public String toModelImport(String name) {
        if(commonIncludes.contains(name))
            return null;
        if(importMapping.containsKey(name))
            return "#include <" + importMapping.get(name) + ">";
        return "#include <" + this.includeDir + "/models/" + name + ".h>";
    }

    /**
     * Optional - OpenAPI type conversion.  This is used to map OpenAPI types in a `Schema` into
     * either language specific types via `typeMapping` or into complex models if there is not a mapping.
     *
     * @return a string value of the type or complex model for this property
     */
    @Override
    @SuppressWarnings("rawtypes")
    public String getSchemaType(Schema p) {
        String openAPIType = super.getSchemaType(p);

        if (typeMapping.containsKey(openAPIType)) {
            return typeMapping.get(openAPIType);
        }
        return toModelName(openAPIType);
    }

    /**
     * Optional - type declaration.  This is a String which is used by the templates to instantiate your
     * types.  There is typically special handling for different property types
     *
     * @return a string value used as the `dataType` field for model templates, `returnType` for api templates
     */
    @Override
    @SuppressWarnings("rawtypes")
    public String getTypeDeclaration(Schema p) {
        if (ModelUtils.isArraySchema(p)) {
            ArraySchema ap = (ArraySchema) p;
            Schema inner = ap.getItems();
            return getSchemaType(p) + "<" + getTypeDeclaration(inner) + ">";
        } else if (ModelUtils.isSet(p)) {
            ArraySchema ap = (ArraySchema) p;
            Schema inner = ap.getItems();
            return getSchemaType(p) + "<" + getTypeDeclaration(inner) + ">";
        } else if (ModelUtils.isMapSchema(p)) {
            Schema inner = getAdditionalProperties(p);
            return getSchemaType(p) + "<" + typeMapping.get("string") + ", " + getTypeDeclaration(inner) + ">";
        } else {
            return getSchemaType(p);
        }
    }

    @Override
    public void processOpts() {
        super.processOpts();

        if (additionalProperties.containsKey("cmakeTarget")) {
            cmakeTarget = (String) additionalProperties.get("cmakeTarget");
        }
        if (additionalProperties.containsKey("cppNamespace")) {
            cppNamespace = (String) additionalProperties.get("cppNamespace");
        }
        if (additionalProperties.containsKey("includeDir")) {
            includeDir = (String) additionalProperties.get("includeDir");
        }
        if (additionalProperties.containsKey("prefix")) {
            prefix = (String) additionalProperties.get("prefix");
        }
        
        apiPackage = "include/" + includeDir + "/apis";
        modelPackage = "include/" + includeDir + "/models";
        supportingFiles.add(new SupportingFile("helper-header.mustache", "src", "Helper.h"));
        supportingFiles.add(new SupportingFile("helper-source.mustache", "src", "Helper.cpp"));
        supportingFiles.add(new SupportingFile("general-header.mustache", "include/" + includeDir, "Client.h"));
        supportingFiles.add(new SupportingFile("cmake.mustache", "", "CMakeLists.txt"));
        supportingFiles.add(new SupportingFile("README.mustache", "", "README.md"));
    }

    @Override
    @SuppressWarnings("static-method")
    public String escapeReservedWord(String name) {
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    @Override
    protected boolean isReservedWord(String word) {
        return word != null && reservedWords.contains(word);
    }

    protected String getHeaderFolder() {
        return "include/" + includeDir;
    }

    protected String getSourceFolder() {
        return "src";
    }

    @Override
    public String apiFilename(String templateName, String tag) {
        String suffix = apiTemplateFiles().get(templateName);
        String targetOutDir = suffix.equals(".h") ? getHeaderFolder() : getSourceFolder();
        return outputFolder + "/" + targetOutDir + "/apis/" + toApiFilename(tag) + suffix;
    }

    @Override
    public String modelFilename(String templateName, String modelName) {
        String suffix = modelTemplateFiles().get(templateName);
        String targetOutDir = suffix.equals(".h") ? getHeaderFolder() : getSourceFolder();
        return outputFolder + "/" + targetOutDir + "/models/" + toModelFilename(modelName) + suffix;
    }
}
