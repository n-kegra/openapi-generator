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

        super.typeMapping = new HashMap<>();
        typeMapping.put("string", "std::string");
        typeMapping.put("integer", "int32_t");
        typeMapping.put("long", "int64_t");
        typeMapping.put("boolean", "bool");
        typeMapping.put("number", "double");
        typeMapping.put("array", "std::vector");
        typeMapping.put("map", "std::map");
        typeMapping.put("set", "std::set");
        typeMapping.put("file", "drogon::UploadFile");

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

        outputFolder = "generated-code" + File.separator + "cpp-drogon";
        modelTemplateFiles.put("model-header.mustache", ".h");
        // modelTemplateFiles.put("model-source.mustache", ".cpp");
        apiTemplateFiles.put("api-header.mustache", ".h");
        // apiTemplateFiles.put("api-source.mustache", ".cpp");
        embeddedTemplateDir = templateDir = "cpp-drogon";
        apiPackage = "apis";
        modelPackage = "models";
        supportingFiles.add(new SupportingFile("helper-header.mustache", "", "Helper.h"));
        supportingFiles.add(new SupportingFile("helper-source.mustache", "", "Helper.cpp"));
        supportingFiles.add(new SupportingFile("README.mustache", "", "README.md"));
    }

    @Override
    public String toModelImport(String name) {
        if(commonIncludes.contains(name))
            return null;
        if(importMapping.containsKey(name))
            return "#include <" + importMapping.get(name) + ">";
        return "#include \"../models/" + name + ".h\"";
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
        } else if (ModelUtils.isMapSchema(p)) {
            Schema inner = getAdditionalProperties(p);
            return getSchemaType(p) + "<" + typeMapping.get("string") + ", " + getTypeDeclaration(inner) + ">";
        } else {
            return getSchemaType(p);
        }
    }
}
