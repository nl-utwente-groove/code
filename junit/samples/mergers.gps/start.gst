<?xml version="1.0" encoding="UTF-8"?>
<gxl xmlns="http://www.gupro.de/GXL/gxl-1.0.dtd">
    <graph id="graph" role="graph" edgeids="false" edgemode="directed">
        <node id="n73"/>
        <node id="n76"/>
        <node id="n74"/>
        <node id="n75"/>
        <node id="n72"/>
        <edge from="n75" to="n73">
            <attr name="label">
                <string>b-to-b</string>
            </attr>
        </edge>
        <edge from="n76" to="n74">
            <attr name="label">
                <string>a-to-a</string>
            </attr>
        </edge>
        <edge from="n76" to="n76">
            <attr name="label">
                <string>a</string>
            </attr>
        </edge>
        <edge from="n75" to="n75">
            <attr name="label">
                <string>b</string>
            </attr>
        </edge>
        <edge from="n72" to="n72">
            <attr name="label">
                <string>c</string>
            </attr>
        </edge>
        <edge from="n74" to="n72">
            <attr name="label">
                <string>a-to-c</string>
            </attr>
        </edge>
        <edge from="n72" to="n73">
            <attr name="label">
                <string>c-to-b</string>
            </attr>
        </edge>
        <edge from="n73" to="n73">
            <attr name="label">
                <string>b</string>
            </attr>
        </edge>
        <edge from="n76" to="n75">
            <attr name="label">
                <string>a-to-b</string>
            </attr>
        </edge>
        <edge from="n74" to="n74">
            <attr name="label">
                <string>a</string>
            </attr>
        </edge>
    </graph>
</gxl>
