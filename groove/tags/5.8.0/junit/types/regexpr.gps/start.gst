<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<gxl xmlns="http://www.gupro.de/GXL/gxl-1.0.dtd">
    <graph edgemode="directed" edgeids="false" role="graph" id="start">
        <attr name="$version">
            <string>curly</string>
        </attr>
        <attr name="$version">
            <string>curly</string>
        </attr>
        <node id="n3">
            <attr name="layout">
                <string>239 325 9 15</string>
            </attr>
        </node>
        <node id="n1">
            <attr name="layout">
                <string>141 324 8 15</string>
            </attr>
        </node>
        <node id="n4">
            <attr name="layout">
                <string>465 198 16 15</string>
            </attr>
        </node>
        <node id="n2">
            <attr name="layout">
                <string>187 115 9 30</string>
            </attr>
        </node>
        <node id="n0">
            <attr name="layout">
                <string>114 211 17 30</string>
            </attr>
        </node>
        <node id="n5">
            <attr name="layout">
                <string>473 76 8 15</string>
            </attr>
        </node>
        <node id="n6">
            <attr name="layout">
                <string>259 208 17 30</string>
            </attr>
        </node>
        <edge to="n3" from="n3">
            <attr name="label">
                <string>type:D</string>
            </attr>
        </edge>
        <edge to="n2" from="n3">
            <attr name="label">
                <string>dToA</string>
            </attr>
            <attr name="layout">
                <string>500 0 243 332 191 130 12</string>
            </attr>
        </edge>
        <edge to="n1" from="n1">
            <attr name="label">
                <string>type:C</string>
            </attr>
        </edge>
        <edge to="n2" from="n1">
            <attr name="label">
                <string>cToA</string>
            </attr>
        </edge>
        <edge to="n4" from="n4">
            <attr name="label">
                <string>type:B1</string>
            </attr>
        </edge>
        <edge to="n6" from="n4">
            <attr name="label">
                <string>b1ToA2</string>
            </attr>
        </edge>
        <edge to="n2" from="n4">
            <attr name="label">
                <string>b1ToA</string>
            </attr>
        </edge>
        <edge to="n2" from="n2">
            <attr name="label">
                <string>type:A</string>
            </attr>
        </edge>
        <edge to="n2" from="n2">
            <attr name="label">
                <string>flag:a</string>
            </attr>
        </edge>
        <edge to="n5" from="n2">
            <attr name="label">
                <string>aToB</string>
            </attr>
        </edge>
        <edge to="n0" from="n0">
            <attr name="label">
                <string>type:A1</string>
            </attr>
        </edge>
        <edge to="n0" from="n0">
            <attr name="label">
                <string>flag:a1</string>
            </attr>
        </edge>
        <edge to="n1" from="n0">
            <attr name="label">
                <string>aTo</string>
            </attr>
        </edge>
        <edge to="n5" from="n0">
            <attr name="label">
                <string>a1ToB</string>
            </attr>
        </edge>
        <edge to="n5" from="n5">
            <attr name="label">
                <string>type:B</string>
            </attr>
        </edge>
        <edge to="n2" from="n5">
            <attr name="label">
                <string>bToA</string>
            </attr>
            <attr name="layout">
                <string>500 0 477 83 334 41 191 130 12</string>
            </attr>
        </edge>
        <edge to="n6" from="n6">
            <attr name="label">
                <string>type:A2</string>
            </attr>
        </edge>
        <edge to="n6" from="n6">
            <attr name="label">
                <string>flag:a2</string>
            </attr>
        </edge>
        <edge to="n5" from="n6">
            <attr name="label">
                <string>a2ToB</string>
            </attr>
        </edge>
        <edge to="n3" from="n6">
            <attr name="label">
                <string>aTo</string>
            </attr>
        </edge>
    </graph>
</gxl>
