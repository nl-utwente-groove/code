<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<gxl xmlns="http://www.gupro.de/GXL/gxl-1.0.dtd">
    <graph role="graph" edgeids="false" edgemode="directed" id="PG1">
        <attr name="$version">
            <string>curly</string>
        </attr>
        <attr name="$version">
            <string>curly</string>
        </attr>
        <node id="n5">
            <attr name="layout">
                <string>225 139 40 18</string>
            </attr>
        </node>
        <node id="n4">
            <attr name="layout">
                <string>322 138 38 18</string>
            </attr>
        </node>
        <node id="n3">
            <attr name="layout">
                <string>347 256 23 18</string>
            </attr>
        </node>
        <node id="n1">
            <attr name="layout">
                <string>341 211 14 18</string>
            </attr>
        </node>
        <node id="n0">
            <attr name="layout">
                <string>235 250 23 18</string>
            </attr>
        </node>
        <edge from="n5" to="n5">
            <attr name="label">
                <string>type:Object</string>
            </attr>
        </edge>
        <edge from="n4" to="n4">
            <attr name="label">
                <string>type:Buffer</string>
            </attr>
        </edge>
        <edge from="n4" to="n0">
            <attr name="label">
                <string>first</string>
            </attr>
            <attr name="layout">
                <string>498 0 330 159 256 247 11</string>
            </attr>
        </edge>
        <edge from="n4" to="n0">
            <attr name="label">
                <string>last</string>
            </attr>
            <attr name="layout">
                <string>498 0 330 159 256 247 11</string>
            </attr>
        </edge>
        <edge from="n4" to="n1">
            <attr name="label">
                <string>empty</string>
            </attr>
            <attr name="layout">
                <string>404 0 348 159 348 206 11</string>
            </attr>
        </edge>
        <edge from="n3" to="n3">
            <attr name="label">
                <string>type:Cell</string>
            </attr>
        </edge>
        <edge from="n1" to="n1">
            <attr name="label">
                <string>int:88</string>
            </attr>
        </edge>
        <edge from="n0" to="n0">
            <attr name="label">
                <string>type:Cell</string>
            </attr>
        </edge>
        <edge from="n0" to="n5">
            <attr name="label">
                <string>val</string>
            </attr>
        </edge>
        <edge from="n0" to="n3">
            <attr name="label">
                <string>next</string>
            </attr>
        </edge>
    </graph>
</gxl>
