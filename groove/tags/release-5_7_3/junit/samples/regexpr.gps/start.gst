<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<gxl xmlns="http://www.gupro.de/GXL/gxl-1.0.dtd">
    <graph edgemode="directed" edgeids="false" role="graph" id="start">
        <node id="n2">
            <attr name="layout">
                <string>255 93 18 18</string>
            </attr>
        </node>
        <node id="n1">
            <attr name="layout">
                <string>183 93 18 18</string>
            </attr>
        </node>
        <node id="n5">
            <attr name="layout">
                <string>261 159 18 18</string>
            </attr>
        </node>
        <node id="n0">
            <attr name="layout">
                <string>93 93 18 18</string>
            </attr>
        </node>
        <node id="n4">
            <attr name="layout">
                <string>171 165 18 18</string>
            </attr>
        </node>
        <node id="n3">
            <attr name="layout">
                <string>345 93 18 18</string>
            </attr>
        </node>
        <node id="n6">
            <attr name="layout">
                <string>357 159 18 18</string>
            </attr>
        </node>
        <edge to="n3" from="n2">
            <attr name="label">
                <string>next</string>
            </attr>
        </edge>
        <edge to="n5" from="n2">
            <attr name="label">
                <string>val</string>
            </attr>
        </edge>
        <edge to="n4" from="n1">
            <attr name="label">
                <string>val</string>
            </attr>
        </edge>
        <edge to="n2" from="n1">
            <attr name="label">
                <string>next</string>
            </attr>
        </edge>
        <edge to="n5" from="n5">
            <attr name="label">
                <string>2</string>
            </attr>
        </edge>
        <edge to="n0" from="n0">
            <attr name="label">
                <string>List</string>
            </attr>
        </edge>
        <edge to="n1" from="n0">
            <attr name="label">
                <string>next</string>
            </attr>
        </edge>
        <edge to="n4" from="n4">
            <attr name="label">
                <string>1</string>
            </attr>
        </edge>
        <edge to="n6" from="n3">
            <attr name="label">
                <string>val</string>
            </attr>
        </edge>
        <edge to="n6" from="n6">
            <attr name="label">
                <string>3</string>
            </attr>
        </edge>
    </graph>
</gxl>
