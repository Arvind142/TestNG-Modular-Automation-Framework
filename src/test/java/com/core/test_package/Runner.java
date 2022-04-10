package com.core.test_package;

import com.core.framework.TestNG_Base;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners(com.core.framework.Listener.class)
public class Runner extends TestNG_Base
{
    @DataProvider(name="default")
    public Object[][] dataProvider(){
        Object[][] arg = new Object[2][2];
        arg[0][0]="A";
        arg[0][1]="B";
        arg[1][0]="C";
        arg[1][1]="D";
        return arg;
    }

    @Test
    public void testNoArgs(){
        System.out.println("Test Executed");
    }

    @Test(dataProvider = "default")
    public void test2Args(String arg1,String arg2){
        System.out.println("Test Executed");
    }

    @Test(dataProvider = "default")
    public void testArgs(String ...arg2){
        System.out.println("Test Executed");
    }
}
