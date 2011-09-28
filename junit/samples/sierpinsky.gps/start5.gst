<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<gxl xmlns="http://www.gupro.de/GXL/gxl-1.0.dtd">
    <graph edgemode="directed" edgeids="false" role="graph" id="start5">
        <node id="n4">
            <attr name="layout">
                <string>105 237 15 15</string>
            </attr>
        </node>
        <node id="n1">
            <attr name="layout">
                <string>342 30 6 14</string>
            </attr>
        </node>
        <node id="n5">
            <attr name="layout">
                <string>508 30 6 14</string>
            </attr>
        </node>
        <node id="n3">
            <attr name="layout">
                <string>602 27 6 14</string>
            </attr>
        </node>
        <node id="n8">
            <attr name="layout">
                <string>425 30 6 14</string>
            </attr>
        </node>
        <node id="n2">
            <attr name="layout">
                <string>240 27 6 14</string>
            </attr>
        </node>
        <node id="n6">
            <attr name="layout">
                <string>205 237 15 15</string>
            </attr>
        </node>
        <node id="n0">
            <attr name="layout">
                <string>141 29 35 14</string>
            </attr>
        </node>
        <node id="n7">
            <attr name="layout">
                <string>153 135 15 15</string>
            </attr>
        </node>
        <edge to="n5" from="n8">
            <attr name="label">
                <string>next</string>
            </attr>
        </edge>
        <edge to="n7" from="n4">
            <attr name="label">
                <string>v</string>
            </attr>
        </edge>
        <edge to="n1" from="n2">
            <attr name="label">
                <string>next</string>
            </attr>
        </edge>
        <edge to="n2" from="n0">
            <attr name="label">
                <string>next</string>
            </attr>
        </edge>
        <edge to="n5" from="n5">
            <attr name="label">
                <string>4</string>
            </attr>
        </edge>
        <edge to="n7" from="n6">
            <attr name="label">
                <string>v</string>
            </attr>
        </edge>
        <edge to="n0" from="n0">
            <attr name="label">
                <string>current</string>
            </attr>
        </edge>
        <edge to="n1" from="n1">
            <attr name="label">
                <string>2</string>
            </attr>
        </edge>
        <edge to="n3" from="n5">
            <attr name="label">
                <string>next</string>
            </attr>
        </edge>
        <edge to="n8" from="n1">
            <attr name="label">
                <string>next</string>
            </attr>
        </edge>
        <edge to="n3" from="n3">
            <attr name="label">
                <string>5</string>
            </attr>
        </edge>
        <edge to="n8" from="n8">
            <attr name="label">
                <string>3</string>
            </attr>
        </edge>
        <edge to="n2" from="n2">
            <attr name="label">
                <string>1</string>
            </attr>
        </edge>
        <edge to="n6" from="n4">
            <attr name="label">
                <string>h</string>
            </attr>
        </edge>
    </graph>
</gxl>
