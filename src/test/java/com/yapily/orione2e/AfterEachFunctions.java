package com.yapily.orione2e;

import com.yapily.orione2e.extension.lifecycle.AfterEach;

@AfterEach(testClass = Test1.class)
public class AfterEachFunctions
{
    @AfterEach
    public void initAfterEachTestInstanceState()
    {
        System.out.println("executed after each test");
    }
}
