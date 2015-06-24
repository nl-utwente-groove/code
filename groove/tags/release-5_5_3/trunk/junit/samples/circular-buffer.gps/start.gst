<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<gxl xmlns="http://www.gupro.de/GXL/gxl-1.0.dtd">
    <graph edgemode="directed" edgeids="false" role="graph" id="start">
        <node id="n5">
            <attr name="layout">
                <string>16 126 30 18</string>
            </attr>
        </node>
        <node id="n8">
            <attr name="layout">
                <string>137 212 30 18</string>
            </attr>
        </node>
        <node id="n4">
            <attr name="layout">
                <string>245 128 30 18</string>
            </attr>
        </node>
        <node id="n7">
            <attr name="layout">
                <string>132 126 40 18</string>
            </attr>
        </node>
        <node id="n6">
            <attr name="layout">
                <string>139 23 30 18</string>
            </attr>
        </node>
        <edge to="n8" from="n4">
            <attr name="label">
                <string>next</string>
            </attr>
        </edge>
        <edge to="n6" from="n6">
            <attr name="label">
                <string>Cell</string>
            </attr>
        </edge>
        <edge to="n5" from="n7">
            <attr name="label">
                <string>last</string>
            </attr>
        </edge>
        <edge to="n4" from="n4">
            <attr name="label">
                <string>Cell</string>
            </attr>
        </edge>
        <edge to="n7" from="n7">
            <attr name="label">
                <string>Buffer</string>
            </attr>
        </edge>
        <edge to="n6" from="n7">
            <attr name="label">
                <string>first</string>
            </attr>
        </edge>
        <edge to="n6" from="n5">
            <attr name="label">
                <string>next</string>
            </attr>
        </edge>
        <edge to="n4" from="n6">
            <attr name="label">
                <string>next</string>
            </attr>
        </edge>
        <edge to="n5" from="n8">
            <attr name="label">
                <string>next</string>
            </attr>
        </edge>
        <edge to="n8" from="n8">
            <attr name="label">
                <string>Cell</string>
            </attr>
        </edge>
        <edge to="n5" from="n5">
            <attr name="label">
                <string>Cell</string>
            </attr>
        </edge>
    </graph>
</gxl>
