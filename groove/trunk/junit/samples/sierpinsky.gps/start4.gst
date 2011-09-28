<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<gxl xmlns="http://www.gupro.de/GXL/gxl-1.0.dtd">
    <graph edgemode="directed" edgeids="false" role="graph" id="start4">
        <attr name="$version">
            <string>curly</string>
        </attr>
        <node id="n4">
            <attr name="layout">
                <string>487 20 27 31</string>
            </attr>
        </node>
        <node id="n7">
            <attr name="layout">
                <string>187 229 51 31</string>
            </attr>
        </node>
        <node id="n3">
            <attr name="layout">
                <string>87 229 51 31</string>
            </attr>
        </node>
        <node id="n5">
            <attr name="layout">
                <string>135 127 51 31</string>
            </attr>
        </node>
        <node id="n1">
            <attr name="layout">
                <string>408 21 39 31</string>
            </attr>
        </node>
        <node id="n2">
            <attr name="layout">
                <string>325 21 39 31</string>
            </attr>
        </node>
        <node id="n6">
            <attr name="layout">
                <string>223 18 39 31</string>
            </attr>
        </node>
        <node id="n0">
            <attr name="layout">
                <string>90 17 68 31</string>
            </attr>
        </node>
        <edge to="n2" from="n6">
            <attr name="label">
                <string>next</string>
            </attr>
        </edge>
        <edge to="n1" from="n2">
            <attr name="label">
                <string>next</string>
            </attr>
        </edge>
        <edge to="n7" from="n7">
            <attr name="label">
                <string>type:Node</string>
            </attr>
        </edge>
        <edge to="n4" from="n1">
            <attr name="label">
                <string>next</string>
            </attr>
        </edge>
        <edge to="n5" from="n5">
            <attr name="label">
                <string>type:Node</string>
            </attr>
        </edge>
        <edge to="n0" from="n0">
            <attr name="label">
                <string>type:counter</string>
            </attr>
        </edge>
        <edge to="n5" from="n7">
            <attr name="label">
                <string>v</string>
            </attr>
        </edge>
        <edge to="n6" from="n6">
            <attr name="label">
                <string>type:cell</string>
            </attr>
        </edge>
        <edge to="n7" from="n3">
            <attr name="label">
                <string>h</string>
            </attr>
        </edge>
        <edge to="n5" from="n3">
            <attr name="label">
                <string>v</string>
            </attr>
        </edge>
        <edge to="n1" from="n1">
            <attr name="label">
                <string>type:cell</string>
            </attr>
        </edge>
        <edge to="n2" from="n2">
            <attr name="label">
                <string>type:cell</string>
            </attr>
        </edge>
        <edge to="n6" from="n0">
            <attr name="label">
                <string>current</string>
            </attr>
        </edge>
        <edge to="n3" from="n3">
            <attr name="label">
                <string>type:Node</string>
            </attr>
        </edge>
        <edge to="n4" from="n4">
            <attr name="label">
                <string>type:cell</string>
            </attr>
        </edge>
    </graph>
</gxl>
