<?xml version="1.0" encoding="UTF-8"?>
<gxl xmlns="http://www.gupro.de/GXL/gxl-1.0.dtd">
    <graph id="start" role="graph" edgeids="false" edgemode="directed">
        <attr name="$version">
            <string>curly</string>
        </attr>
        <node id="n1"/>
        <node id="n2"/>
        <node id="n4"/>
        <node id="n5"/>
        <node id="n3"/>
        <node id="n0"/>
        <edge from="n2" to="n2">
            <attr name="label">
                <string>int:0</string>
            </attr>
        </edge>
        <edge from="n5" to="n5">
            <attr name="label">
                <string>bool:false</string>
            </attr>
        </edge>
        <edge from="n3" to="n3">
            <attr name="label">
                <string>int:0</string>
            </attr>
        </edge>
        <edge from="n1" to="n1">
            <attr name="label">
                <string>B</string>
            </attr>
        </edge>
        <edge from="n4" to="n5">
            <attr name="label">
                <string>finished</string>
            </attr>
        </edge>
        <edge from="n0" to="n2">
            <attr name="label">
                <string>score</string>
            </attr>
        </edge>
        <edge from="n1" to="n3">
            <attr name="label">
                <string>score</string>
            </attr>
        </edge>
        <edge from="n4" to="n4">
            <attr name="label">
                <string>Properties</string>
            </attr>
        </edge>
        <edge from="n0" to="n0">
            <attr name="label">
                <string>A</string>
            </attr>
        </edge>
    </graph>
</gxl>
