package com.yapily.orione2e;

import com.yapily.orione2e.extension.lifecycle.Given;

@Given(testClass = Test1.class)
public class GivenFunctions
{
    @Given
    public void initPerTestInstanceState()
    {
        System.out.println("executed before each test");
    }
}
