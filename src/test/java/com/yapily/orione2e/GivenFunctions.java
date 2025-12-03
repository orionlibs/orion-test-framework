package com.yapily.orione2e;

import com.yapily.orione2e.lifecycle.Given;

@Given
public class GivenFunctions
{
    @Given
    public void initPerTestInstanceState()
    {
        System.out.println(">>>>>executed before each test");
    }
}
