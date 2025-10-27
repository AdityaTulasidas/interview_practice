package com.thomsonreuters.dataconnect.dataintegration.utils;

import com.thomsonreuters.dataconnect.dataintegration.dto.*;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * Utility class for trimming whitespace from payload string fields
 */
public class PayloadTrimmingUtil {

    /**
     * Trims all string fields in DatasyncJobConfigurationRequestDTO
     * @param dto the DTO to trim
     */
    public static void trimJobConfigurationRequest(DatasyncJobConfigurationRequestDTO dto) {
        if (dto == null) return;
        
        dto.setDescription(trimToNull(dto.getDescription()));
        dto.setMetaObjectSysName(trimToNull(dto.getMetaObjectSysName()));
        dto.setCustomerTenantSysName(trimToNull(dto.getCustomerTenantSysName()));
        dto.setSystemName(trimToNull(dto.getSystemName()));
        dto.setOnesourceDomain(trimToNull(dto.getOnesourceDomain()));
        
        // Trim source region fields
        if (dto.getSource() != null) {
            trimSourceRegion(dto.getSource());
        }
        
        // Trim target regions
        if (dto.getTargets() != null) {
            dto.getTargets().forEach(PayloadTrimmingUtil::trimTargetRegion);
        }
        
        // Trim transformations
        if (dto.getTransformations() != null) {
            dto.getTransformations().forEach(PayloadTrimmingUtil::trimTransformation);
        }
        
        // Trim activities
        if (dto.getActivities() != null) {
            dto.getActivities().forEach(PayloadTrimmingUtil::trimActivity);
        }
    }

    /**
     * Trims all string fields in DatasyncJobConfigurationUpdateRequestDTO
     * @param dto the DTO to trim
     */
    public static void trimJobConfigurationUpdate(DatasyncJobConfigurationUpdateRequestDTO dto) {
        if (dto == null) return;
        
        dto.setDescription(trimToNull(dto.getDescription()));
        dto.setMetaObjectSysName(trimToNull(dto.getMetaObjectSysName()));
        dto.setCustomerTenantSysName(trimToNull(dto.getCustomerTenantSysName()));
        dto.setSystemName(trimToNull(dto.getSystemName()));
        dto.setOnesourceDomain(trimToNull(dto.getOnesourceDomain()));
        
        // Trim source regional tenant
        if (dto.getSource() != null) {
            trimSourceRegionalTenant(dto.getSource());
        }
        
        // Trim target regions
        if (dto.getTargets() != null) {
            dto.getTargets().forEach(PayloadTrimmingUtil::trimTargetRegion);
        }
        
        // Trim transformations
        if (dto.getTransformations() != null) {
            dto.getTransformations().forEach(PayloadTrimmingUtil::trimTransformation);
        }
        
        // Trim activities
        if (dto.getActivities() != null) {
            dto.getActivities().forEach(PayloadTrimmingUtil::trimActivity);
        }
    }

    /**
     * Trims string fields in SourceRegionDTO
     * @param dto the DTO to trim
     */
    public static void trimSourceRegion(SourceRegionDTO dto) {
        if (dto == null) return;
        dto.setRegion(trimToNull(dto.getRegion()));
        dto.setRegionalTenantId(trimToNull(dto.getRegionalTenantId()));
    }

    /**
     * Trims string fields in TargetRegionDTO
     * @param dto the DTO to trim
     */
    public static void trimTargetRegion(TargetRegionDTO dto) {
        if (dto == null) return;
        dto.setRegion(trimToNull(dto.getRegion()));
        dto.setRegionalTenantId(trimToNull(dto.getRegionalTenantId()));
    }

    /**
     * Trims string fields in SourceRegionalTenantDTO
     * @param dto the DTO to trim
     */
    public static void trimSourceRegionalTenant(SourceRegionalTenantDTO dto) {
        if (dto == null) return;
        dto.setRegionalTenantId(trimToNull(dto.getRegionalTenantId()));
    }

    /**
     * Trims string fields in RegionalTenantDTO
     * @param dto the DTO to trim
     */
    public static void trimRegionalTenant(RegionalTenantDTO dto) {
        if (dto == null) return;
        dto.setId(trimToNull(dto.getId()));
        dto.setTenant_code(trimToNull(dto.getTenant_code()));
        dto.setCustomer_tenant_id(trimToNull(dto.getCustomer_tenant_id()));
        dto.setRegion(trimToNull(dto.getRegion()));
        dto.setTenant_name(trimToNull(dto.getTenant_name()));
        dto.setCreated_by(trimToNull(dto.getCreated_by()));
        dto.setCreated_at(trimToNull(dto.getCreated_at()));
    }

    /**
     * Trims string fields in a list of RegionalTenantDTO
     * @param dtoList the list of DTOs to trim
     */
    public static void trimRegionalTenants(List<RegionalTenantDTO> dtoList) {
        if (dtoList != null) {
            dtoList.forEach(PayloadTrimmingUtil::trimRegionalTenant);
        }
    }

    /**
     * Trims string fields in CustomerTenantDTO
     * @param dto the DTO to trim
     */
    public static void trimCustomerTenant(CustomerTenantDTO dto) {
        if (dto == null) return;
        
        // Trim fields using reflection or direct access
        // Note: CustomerTenantDTO fields would need to be trimmed based on its structure
        if (dto.getRegional_tenants() != null) {
            trimRegionalTenants(dto.getRegional_tenants());
        }
    }

    /**
     * Trims transformation fields
     * @param transformation the transformation to trim
     */
    private static void trimTransformation(Transformations transformation) {
        if (transformation == null) return;
        
        transformation.setFuncName(trimToNull(transformation.getFuncName()));
        transformation.setRegion(trimToNull(transformation.getRegion()));
        transformation.setExecLeg(trimToNull(transformation.getExecLeg()));
        
        // Trim parameters
        if (transformation.getParams() != null) {
            transformation.getParams().forEach(PayloadTrimmingUtil::trimTransformParams);
        }
    }

    /**
     * Trims activity fields
     * @param activity the activity to trim
     */
    private static void trimActivity(ActivityDTO activity) {
        if (activity == null) return;
        
        activity.setSysName(trimToNull(activity.getSysName()));
        activity.setExecType(trimToNull(activity.getExecType()));
        activity.setEventType(trimToNull(activity.getEventType()));
        activity.setActivityType(trimToNull(activity.getActivityType()));
    }

    /**
     * Trims TransformParams fields
     * @param params the transform params to trim
     */
    private static void trimTransformParams(TransformParams params) {
        if (params == null) return;
        
        params.setName(trimToNull(params.getName()));
        params.setValue(trimToNull(params.getValue()));
    }

    /**
     * Trims a string and returns null if empty after trimming
     * @param value the string to trim
     * @return trimmed string or null if empty/null
     */
    private static String trimToNull(String value) {
        return StringUtils.trimToNull(value);
    }
}
