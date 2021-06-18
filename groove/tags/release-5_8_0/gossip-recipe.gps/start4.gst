<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<gxl xmlns="http://www.gupro.de/GXL/gxl-1.0.dtd">
    <graph edgemode="directed" edgeids="false" role="graph" id="start4">
        <node id="n0">
            <attr name="layout">
                <string>136 155 15 14</string>
            </attr>
        </node>
        <node id="n3">
            <attr name="layout">
                <string>205 158 15 14</string>
            </attr>
        </node>
        <node id="n2">
            <attr name="layout">
                <string>270 156 15 14</string>
            </attr>
        </node>
        <node id="n4">
            <attr name="layout">
                <string>131 234 31 14</string>
            </attr>
        </node>
        <node id="n6">
            <attr name="layout">
                <string>73 154 15 14</string>
            </attr>
        </node>
        <node id="n1">
            <attr name="layout">
                <string>69 234 31 14</string>
            </attr>
        </node>
        <node id="n5">
            <attr name="layout">
                <string>260 236 31 14</string>
            </attr>
        </node>
        <node id="n7">
            <attr name="layout">
                <string>194 237 31 14</string>
            </attr>
        </node>
        <edge to="n0" from="n0">
            <attr name="label">
                <string>type:Girl</string>
            </attr>
        </edge>
        <edge to="n4" from="n0">
            <attr name="label">
                <string>knows</string>
            </attr>
        </edge>
        <edge to="n7" from="n3">
            <attr name="label">
                <string>knows</string>
            </attr>
        </edge>
        <edge to="n3" from="n3">
            <attr name="label">
                <string>type:Girl</string>
            </attr>
        </edge>
        <edge to="n5" from="n2">
            <attr name="label">
                <string>knows</string>
            </attr>
        </edge>
        <edge to="n2" from="n2">
            <attr name="label">
                <string>type:Girl</string>
            </attr>
        </edge>
        <edge to="n4" from="n4">
            <attr name="label">
                <string>type:Secret</string>
            </attr>
        </edge>
        <edge to="n1" from="n6">
            <attr name="label">
                <string>knows</string>
            </attr>
        </edge>
        <edge to="n6" from="n6">
            <attr name="label">
                <string>type:Girl</string>
            </attr>
        </edge>
        <edge to="n1" from="n1">
            <attr name="label">
                <string>type:Secret</string>
            </attr>
        </edge>
        <edge to="n5" from="n5">
            <attr name="label">
                <string>type:Secret</string>
            </attr>
        </edge>
        <edge to="n7" from="n7">
            <attr name="label">
                <string>type:Secret</string>
            </attr>
        </edge>
    </graph>
</gxl>
