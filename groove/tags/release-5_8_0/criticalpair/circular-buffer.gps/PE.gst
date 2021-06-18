<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<gxl xmlns="http://www.gupro.de/GXL/gxl-1.0.dtd">
    <graph role="graph" edgeids="false" edgemode="directed" id="PE">
        <attr name="$version">
            <string>curly</string>
        </attr>
        <node id="n3">
            <attr name="layout">
                <string>124 15 38 18</string>
            </attr>
        </node>
        <node id="n2">
            <attr name="layout">
                <string>69 74 23 18</string>
            </attr>
        </node>
        <node id="n5">
            <attr name="layout">
                <string>197 75 23 18</string>
            </attr>
        </node>
        <node id="n4">
            <attr name="layout">
                <string>61 157 40 18</string>
            </attr>
        </node>
        <edge from="n3" to="n3">
            <attr name="label">
                <string>type:Buffer</string>
            </attr>
        </edge>
        <edge from="n3" to="n2">
            <attr name="label">
                <string>last</string>
            </attr>
        </edge>
        <edge from="n3" to="n5">
            <attr name="label">
                <string>first</string>
            </attr>
        </edge>
        <edge from="n2" to="n2">
            <attr name="label">
                <string>type:Cell</string>
            </attr>
        </edge>
        <edge from="n2" to="n4">
            <attr name="label">
                <string>val</string>
            </attr>
        </edge>
        <edge from="n2" to="n5">
            <attr name="label">
                <string>next</string>
            </attr>
        </edge>
        <edge from="n5" to="n5">
            <attr name="label">
                <string>type:Cell</string>
            </attr>
        </edge>
        <edge from="n5" to="n5">
            <attr name="label">
                <string>flag:empty</string>
            </attr>
        </edge>
        <edge from="n4" to="n4">
            <attr name="label">
                <string>type:Object</string>
            </attr>
        </edge>
    </graph>
</gxl>
