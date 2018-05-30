<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<gxl xmlns="http://www.gupro.de/GXL/gxl-1.0.dtd">
    <graph role="graph" edgeids="false" edgemode="directed" id="ReleaseLL">
        <attr name="$version">
            <string>curly</string>
        </attr>
        <attr name="$version">
            <string>curly</string>
        </attr>
        <node id="n0">
            <attr name="layout">
                <string>197 113 27 18</string>
            </attr>
        </node>
        <node id="n1">
            <attr name="layout">
                <string>197 202 24 36</string>
            </attr>
        </node>
        <node id="n2">
            <attr name="layout">
                <string>288 210 27 18</string>
            </attr>
        </node>
        <edge from="n0" to="n0">
            <attr name="label">
                <string>type:Fork</string>
            </attr>
        </edge>
        <edge from="n1" to="n1">
            <attr name="label">
                <string>type:Phil</string>
            </attr>
        </edge>
        <edge from="n1" to="n1">
            <attr name="label">
                <string>flag:eat</string>
            </attr>
        </edge>
        <edge from="n1" to="n0">
            <attr name="label">
                <string>hold</string>
            </attr>
        </edge>
        <edge from="n1" to="n0">
            <attr name="label">
                <string>left</string>
            </attr>
        </edge>
        <edge from="n1" to="n2">
            <attr name="label">
                <string>hold</string>
            </attr>
        </edge>
        <edge from="n1" to="n2">
            <attr name="label">
                <string>left</string>
            </attr>
        </edge>
        <edge from="n2" to="n2">
            <attr name="label">
                <string>type:Fork</string>
            </attr>
        </edge>
    </graph>
</gxl>
