<?xml version="1.0" encoding="UTF-8"?>
<gxl xmlns="http://www.gupro.de/GXL/gxl-1.0.dtd">
    <graph id="graph" role="graph" edgeids="false" edgemode="directed">
        <node id="n8"/>
        <node id="n6"/>
        <node id="n4"/>
        <node id="n7"/>
        <node id="n5"/>
        <edge from="n7" to="n5">
            <attr name="label">
                <string>last</string>
            </attr>
        </edge>
        <edge from="n6" to="n6">
            <attr name="label">
                <string>Cell</string>
            </attr>
        </edge>
        <edge from="n5" to="n5">
            <attr name="label">
                <string>Cell</string>
            </attr>
        </edge>
        <edge from="n8" to="n8">
            <attr name="label">
                <string>Cell</string>
            </attr>
        </edge>
        <edge from="n8" to="n5">
            <attr name="label">
                <string>next</string>
            </attr>
        </edge>
        <edge from="n6" to="n4">
            <attr name="label">
                <string>next</string>
            </attr>
        </edge>
        <edge from="n7" to="n6">
            <attr name="label">
                <string>first</string>
            </attr>
        </edge>
        <edge from="n4" to="n8">
            <attr name="label">
                <string>next</string>
            </attr>
        </edge>
        <edge from="n5" to="n6">
            <attr name="label">
                <string>next</string>
            </attr>
        </edge>
        <edge from="n4" to="n4">
            <attr name="label">
                <string>Cell</string>
            </attr>
        </edge>
        <edge from="n7" to="n7">
            <attr name="label">
                <string>Buffer</string>
            </attr>
        </edge>
    </graph>
</gxl>
