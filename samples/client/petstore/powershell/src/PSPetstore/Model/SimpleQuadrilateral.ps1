#
# OpenAPI Petstore
# This spec is mainly for testing Petstore server and contains fake endpoints, models. Please do not use this for any other purpose. Special characters: "" \
# Version: 1.0.0
# Generated by OpenAPI Generator: https://openapi-generator.tech
#

<#
.SYNOPSIS

No summary available.

.DESCRIPTION

No description available.

.PARAMETER ShapeType
No description available.
.PARAMETER QuadrilateralType
No description available.
.OUTPUTS

SimpleQuadrilateral<PSCustomObject>
#>

function Initialize-PSSimpleQuadrilateral {
    [CmdletBinding()]
    Param (
        [Parameter(Position = 0, ValueFromPipelineByPropertyName = $true)]
        [String]
        ${ShapeType},
        [Parameter(Position = 1, ValueFromPipelineByPropertyName = $true)]
        [String]
        ${QuadrilateralType}
    )

    Process {
        'Creating PSCustomObject: PSPetstore => PSSimpleQuadrilateral' | Write-Debug
        $PSBoundParameters | Out-DebugParameter | Write-Debug

        if ($null -eq $ShapeType) {
            throw "invalid value for 'ShapeType', 'ShapeType' cannot be null."
        }

        if ($null -eq $QuadrilateralType) {
            throw "invalid value for 'QuadrilateralType', 'QuadrilateralType' cannot be null."
        }


        $PSO = [PSCustomObject]@{
            "shapeType" = ${ShapeType}
            "quadrilateralType" = ${QuadrilateralType}
        }


        return $PSO
    }
}

<#
.SYNOPSIS

Convert from JSON to SimpleQuadrilateral<PSCustomObject>

.DESCRIPTION

Convert from JSON to SimpleQuadrilateral<PSCustomObject>

.PARAMETER Json

Json object

.OUTPUTS

SimpleQuadrilateral<PSCustomObject>
#>
function ConvertFrom-PSJsonToSimpleQuadrilateral {
    Param(
        [AllowEmptyString()]
        [string]$Json
    )

    Process {
        'Converting JSON to PSCustomObject: PSPetstore => PSSimpleQuadrilateral' | Write-Debug
        $PSBoundParameters | Out-DebugParameter | Write-Debug

        $JsonParameters = ConvertFrom-Json -InputObject $Json

        # check if Json contains properties not defined in PSSimpleQuadrilateral
        $AllProperties = ("shapeType", "quadrilateralType")
        foreach ($name in $JsonParameters.PsObject.Properties.Name) {
            if (!($AllProperties.Contains($name))) {
                throw "Error! JSON key '$name' not found in the properties: $($AllProperties)"
            }
        }

        If ([string]::IsNullOrEmpty($Json) -or $Json -eq "{}") { # empty json
            throw "Error! Empty JSON cannot be serialized due to the required property 'shapeType' missing."
        }

        if (!([bool]($JsonParameters.PSobject.Properties.name -match "shapeType"))) {
            throw "Error! JSON cannot be serialized due to the required property 'shapeType' missing."
        } else {
            $ShapeType = $JsonParameters.PSobject.Properties["shapeType"].value
        }

        if (!([bool]($JsonParameters.PSobject.Properties.name -match "quadrilateralType"))) {
            throw "Error! JSON cannot be serialized due to the required property 'quadrilateralType' missing."
        } else {
            $QuadrilateralType = $JsonParameters.PSobject.Properties["quadrilateralType"].value
        }

        $PSO = [PSCustomObject]@{
            "shapeType" = ${ShapeType}
            "quadrilateralType" = ${QuadrilateralType}
        }

        return $PSO
    }

}

