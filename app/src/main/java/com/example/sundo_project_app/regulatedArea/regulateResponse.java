package com.example.sundo_project_app.regulatedArea;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import java.util.List;

@Root(name = "response", strict = false)
public class regulateResponse {

    @ElementList(name = "items", inline = true)
    private List<Item> items;

    public List<Item> getItems() {
        return items;
    }

    @Root(name = "item", strict = false)
    public static class Item {

        @Element(name = "hmpgAddLcAddr")
        private String hmpgAddLcaddr;

        public String getHmpgAddLcaddr() {
            return hmpgAddLcaddr;
        }
    }
}
