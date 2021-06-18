<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<gxl xmlns="http://www.gupro.de/GXL/gxl-1.0.dtd">
    <graph role="graph" edgeids="false" edgemode="directed" id="EP">
        <attr name="transitionLabel">
            <string></string>
        </attr>
        <attr name="enabled">
            <string>true</string>
        </attr>
        <attr name="priority">
            <string>0</string>
        </attr>
        <attr name="printFormat">
            <string></string>
        </attr>
        <attr name="remark">
            <string></string>
        </attr>
        <attr name="$version">
            <string>curly</string>
        </attr>
        <node id="n4">
            <attr name="layout">
                <string>299 180 38 18</string>
            </attr>
        </node>
        <node id="n3">
            <attr name="layout">
                <string>335 286 23 18</string>
            </attr>
        </node>
        <node id="n1">
            <attr name="layout">
                <string>312 98 14 18</string>
            </attr>
        </node>
        <node id="n0">
            <attr name="layout">
                <string>223 286 23 18</string>
            </attr>
        </node>
        <node id="n5">
            <attr name="layout">
                <string>214 184 40 18</string>
            </attr>
        </node>
        <edge from="n4" to="n4">
            <attr name="label">
                <string>type:Buffer</string>
            </attr>
        </edge>
        <edge from="n4" to="n1">
            <attr name="label">
                <string>empty</string>
            </attr>
        </edge>
        <edge from="n4" to="n3">
            <attr name="label">
                <string>first</string>
            </attr>
        </edge>
        <edge from="n4" to="n0">
            <attr name="label">
                <string>last</string>
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
        <edge from="n0" to="n3">
            <attr name="label">
                <string>next</string>
            </attr>
        </edge>
        <edge from="n0" to="n5">
            <attr name="label">
                <string>val</string>
            </attr>
        </edge>
        <edge from="n5" to="n5">
            <attr name="label">
                <string>type:Object</string>
            </attr>
        </edge>
    </graph>
</gxl>
