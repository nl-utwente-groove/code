<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<gxl xmlns="http://www.gupro.de/GXL/gxl-1.0.dtd">
    <graph edgemode="directed" edgeids="false" role="graph" id="start">
        <node id="n1">
            <attr name="layout">
                <string>290 173 7 16</string>
            </attr>
        </node>
        <node id="n2">
            <attr name="layout">
                <string>366 175 7 16</string>
            </attr>
        </node>
        <node id="n0">
            <attr name="layout">
                <string>217 171 7 16</string>
            </attr>
        </node>
        <node id="n4">
            <attr name="layout">
                <string>79 168 7 16</string>
            </attr>
        </node>
        <node id="n3">
            <attr name="layout">
                <string>147 168 7 16</string>
            </attr>
        </node>
        <edge to="n2" from="n2">
            <attr name="label">
                <string>c</string>
            </attr>
        </edge>
        <edge to="n1" from="n1">
            <attr name="label">
                <string>c</string>
            </attr>
        </edge>
        <edge to="n4" from="n4">
            <attr name="label">
                <string>c</string>
            </attr>
        </edge>
        <edge to="n3" from="n3">
            <attr name="label">
                <string>c</string>
            </attr>
        </edge>
        <edge to="n1" from="n0">
            <attr name="label">
                <string>n</string>
            </attr>
        </edge>
        <edge to="n0" from="n3">
            <attr name="label">
                <string>n</string>
            </attr>
        </edge>
        <edge to="n0" from="n0">
            <attr name="label">
                <string>c</string>
            </attr>
        </edge>
        <edge to="n3" from="n4">
            <attr name="label">
                <string>n</string>
            </attr>
        </edge>
        <edge to="n2" from="n1">
            <attr name="label">
                <string>n</string>
            </attr>
        </edge>
    </graph>
</gxl>
