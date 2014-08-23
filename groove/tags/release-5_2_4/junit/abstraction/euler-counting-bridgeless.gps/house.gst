<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<gxl xmlns="http://www.gupro.de/GXL/gxl-1.0.dtd">
    <graph edgemode="directed" edgeids="false" role="graph" id="house">
        <attr name="$version">
            <string>curly</string>
        </attr>
        <node id="n7">
            <attr name="layout">
                <string>75 131 48 46</string>
            </attr>
        </node>
        <node id="n9">
            <attr name="layout">
                <string>75 286 48 46</string>
            </attr>
        </node>
        <node id="n0">
            <attr name="layout">
                <string>310 285 48 46</string>
            </attr>
        </node>
        <node id="n1">
            <attr name="layout">
                <string>49 24 51 46</string>
            </attr>
        </node>
        <node id="n3">
            <attr name="layout">
                <string>311 132 48 46</string>
            </attr>
        </node>
        <node id="n5">
            <attr name="layout">
                <string>186 17 48 46</string>
            </attr>
        </node>
        <edge to="n0" from="n0">
            <attr name="label">
                <string>flag:odd</string>
            </attr>
        </edge>
        <edge to="n7" from="n7">
            <attr name="label">
                <string>type:Area</string>
            </attr>
        </edge>
        <edge to="n3" from="n3">
            <attr name="label">
                <string>type:Area</string>
            </attr>
        </edge>
        <edge to="n9" from="n9">
            <attr name="label">
                <string>flag:odd</string>
            </attr>
        </edge>
        <edge to="n5" from="n5">
            <attr name="label">
                <string>flag:even</string>
            </attr>
        </edge>
        <edge to="n1" from="n1">
            <attr name="label">
                <string>flag:home</string>
            </attr>
        </edge>
        <edge to="n0" from="n0">
            <attr name="label">
                <string>type:Area</string>
            </attr>
        </edge>
        <edge to="n1" from="n1">
            <attr name="label">
                <string>type:Euler</string>
            </attr>
        </edge>
        <edge to="n3" from="n3">
            <attr name="label">
                <string>flag:even</string>
            </attr>
        </edge>
        <edge to="n5" from="n5">
            <attr name="label">
                <string>type:Area</string>
            </attr>
        </edge>
        <edge to="n9" from="n9">
            <attr name="label">
                <string>type:Area</string>
            </attr>
        </edge>
        <edge to="n7" from="n7">
            <attr name="label">
                <string>flag:even</string>
            </attr>
        </edge>
    </graph>
</gxl>
