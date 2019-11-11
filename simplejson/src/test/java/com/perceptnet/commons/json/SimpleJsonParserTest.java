package com.perceptnet.commons.json;

import com.perceptnet.commons.json.dto.TemplateShortDto;
import com.perceptnet.commons.json.dto.TemplatesGroupDto;
import com.perceptnet.commons.json.parsing.ObjectInfo;
import com.perceptnet.commons.json.parsing.ObjectInfoImpl;
import com.perceptnet.commons.json.parsing.SimpleJsonParser;
import com.perceptnet.commons.reflection.BeanReflectionProviderCachingImpl;
import com.perceptnet.commons.reflection.ReflectionProvider;
import com.perceptnet.commons.utils.ResourceUtils;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.perceptnet.commons.tests.TestGroups.*;

/**
 * created by vkorovkin (vkorovkin@gmail.com) on 04.11.2019
 */
public class SimpleJsonParserTest {

    @Test(groups = {DEBUG})
    public void testArgListParsing() throws Exception {
        ReflectionProvider rp = new BeanReflectionProviderCachingImpl();
        String testJson = ResourceUtils.resourceText("TestJsonA1.json");
        SimpleJsonParser p = new SimpleJsonParser(new StringReader(testJson));
        p.setReflectionProvider(rp);
        List<ObjectInfoImpl> argTypesList = Arrays.asList(
                new ObjectInfoImpl(String.class),
                new ObjectInfoImpl(Date.class),
                new ObjectInfoImpl(Long.class),
                new ObjectInfoImpl(Integer.class),
                new ObjectInfoImpl(Boolean.class),
                new ObjectInfoImpl(Double.class),
                new ObjectInfoImpl(TemplatesGroupDto.class));
        p.setExpectedTopLevelItems(new ObjectInfoImpl(List.class).setCollectionItemsInfos(argTypesList));
        p.any();
        List result = p.getParsedTopLevelObjects();
        System.out.println("Parsing result:\n" + result );

        List list = (List) result.get(0);
        assertEquals(list.size(), argTypesList.size(), "Wrong number of items in parsing result");
        for (int i = 0; i < argTypesList.size(); i++) {
            Object parsedItem = list.get(i);
            if (parsedItem == null) {
                continue;
            }
            assertEquals(parsedItem.getClass(), argTypesList.get(i).getClazz(), "Not expected class of parsed item " + i);
        }
        TemplatesGroupDto g = (TemplatesGroupDto) list.get(6);

        System.out.println("Items in group: " + g.getItems().size());

//        Object readValue = reader.readValue(testJson);
//        System.out.println("Read value: " + readValue);
//        System.out.println("Read value 1: " + readValue);
    }

    private void prepareTestJson1() throws Exception {
        List items = new ArrayList();
        items.add("Vasia Pupkin");
        items.add(new Date());
        items.add(1001L);
        items.add(2019);
        items.add(Boolean.TRUE);
        items.add(-0.001);
        TemplatesGroupDto g = new TemplatesGroupDto();
        g.setGroupName("Group A");
        g.setId(1L);
        items.add(g);

        TemplateShortDto t = new TemplateShortDto();
        t.setId(null);
        t.setCode("Code1");
        t.setCreatedAt(new Date());
        t.setDescription("This is a template 1 blah");
        t.setRank(12);
        t.setPreviewImageUrl("n/a");
        g.getItems().add(t);

        t = new TemplateShortDto();
        t.setId(1L);
        t.setCode("Code2");
        t.setCreatedAt(new Date());
        t.setDescription("This is a template 2 blah-blah");
        t.setRank(2);
        t.setPreviewImageUrl("n/a");
        g.getItems().add(t);

        //System.out.println("Test Json:\n--------------\n" + );

    }



}
