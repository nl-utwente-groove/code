<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<gxl xmlns="http://www.gupro.de/GXL/gxl-1.0.dtd">
    <graph role="graph" edgeids="false" edgemode="directed" id="PP-TODO">
        <attr name="$version">
            <string>curly</string>
        </attr>
        <node id="n0">
            <attr name="layout">
                <string>219 193 34 36</string>
            </attr>
        </node>
        <node id="n1">
            <attr name="layout">
                <string>267 283 38 18</string>
            </attr>
        </node>
        <node id="n2">
            <attr name="layout">
                <string>179 279 38 18</string>
            </attr>
        </node>
        <edge from="n0" to="n0">
            <attr name="label">
                <string>type:Cell</string>
            </attr>
        </edge>
        <edge from="n0" to="n0">
            <attr name="label">
                <string>flag:empty</string>
            </attr>
        </edge>
        <edge from="n0" to="n0">
            <attr name="label">
                <string>next</string>
            </attr>
            <attr name="layout">
                <string>500 -5 260 179 273 161 260 179 11</string>
            </attr>
        </edge>
        <edge from="n1" to="n1">
            <attr name="label">
                <string>type:Buffer</string>
            </attr>
        </edge>
        <edge from="n1" to="n0">
            <attr name="label">
                <string>last</string>
            </attr>
        </edge>
        <edge from="n2" to="n2">
            <attr name="label">
                <string>type:Buffer</string>
            </attr>
        </edge>
        <edge from="n2" to="n0">
            <attr name="label">
                <string>last</string>
            </attr>
        </edge>
    </graph>
</gxl>
