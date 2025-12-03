package com.yapily.orione2e;

import com.yapily.orione2e.glue.Glue;
import com.yapily.orione2e.utils.YAMLProcessor;
import tools.jackson.dataformat.yaml.YAMLMapper;

@Glue
public class GlueFunctions
{
    @Glue
    public static void initialiseE2ETests()
    {
        YAMLProcessor.yamlMapper = YAMLMapper.builder().build();
        System.out.println("Glue components initialised");
    }
}
