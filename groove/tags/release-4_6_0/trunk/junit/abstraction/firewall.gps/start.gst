<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<gxl xmlns="http://www.gupro.de/GXL/gxl-1.0.dtd">
    <graph edgemode="directed" edgeids="false" role="graph" id="start">
        <attr name="$version">
            <string>curly</string>
        </attr>
        <attr name="$version">
            <string>curly</string>
        </attr>
        <node id="n9">
            <attr name="layout">
                <string>103 85 36 64</string>
            </attr>
        </node>
        <node id="n2">
            <attr name="layout">
                <string>350 121 36 48</string>
            </attr>
        </node>
        <node id="n0">
            <attr name="layout">
                <string>271 123 39 48</string>
            </attr>
        </node>
        <node id="n6">
            <attr name="layout">
                <string>427 66 36 64</string>
            </attr>
        </node>
        <node id="n1">
            <attr name="layout">
                <string>188 122 36 48</string>
            </attr>
        </node>
        <node id="n3">
            <attr name="layout">
                <string>430 175 36 64</string>
            </attr>
        </node>
        <node id="n4">
            <attr name="layout">
                <string>101 203 36 64</string>
            </attr>
        </node>
        <node id="n5">
            <attr name="layout">
                <string>525 110 36 64</string>
            </attr>
        </node>
        <node id="n7">
            <attr name="layout">
                <string>14 142 36 64</string>
            </attr>
        </node>
        <edge to="n9" from="n9">
            <attr name="label">
                <string>type:L</string>
            </attr>
        </edge>
        <edge to="n9" from="n9">
            <attr name="label">
                <string>flag:i</string>
            </attr>
        </edge>
        <edge to="n1" from="n9">
            <attr name="label">
                <string>c</string>
            </attr>
        </edge>
        <edge to="n4" from="n9">
            <attr name="label">
                <string>c</string>
            </attr>
            <attr name="layout">
                <string>500 0 121 117 87 176 119 235 12</string>
            </attr>
        </edge>
        <edge to="n7" from="n9">
            <attr name="label">
                <string>c</string>
            </attr>
            <attr name="layout">
                <string>500 0 121 117 60 119 32 174 12</string>
            </attr>
        </edge>
        <edge to="n2" from="n2">
            <attr name="label">
                <string>type:IF</string>
            </attr>
        </edge>
        <edge to="n0" from="n0">
            <attr name="label">
                <string>type:FW</string>
            </attr>
        </edge>
        <edge to="n1" from="n0">
            <attr name="label">
                <string>in</string>
            </attr>
        </edge>
        <edge to="n2" from="n0">
            <attr name="label">
                <string>out</string>
            </attr>
        </edge>
        <edge to="n6" from="n6">
            <attr name="label">
                <string>type:L</string>
            </attr>
        </edge>
        <edge to="n6" from="n6">
            <attr name="label">
                <string>flag:o</string>
            </attr>
        </edge>
        <edge to="n2" from="n6">
            <attr name="label">
                <string>c</string>
            </attr>
        </edge>
        <edge to="n3" from="n6">
            <attr name="label">
                <string>c</string>
            </attr>
            <attr name="layout">
                <string>500 0 445 98 421 153 442 208 12</string>
            </attr>
        </edge>
        <edge to="n5" from="n6">
            <attr name="label">
                <string>c</string>
            </attr>
            <attr name="layout">
                <string>500 0 445 98 482 148 543 142 12</string>
            </attr>
        </edge>
        <edge to="n1" from="n1">
            <attr name="label">
                <string>type:IF</string>
            </attr>
        </edge>
        <edge to="n3" from="n3">
            <attr name="label">
                <string>type:L</string>
            </attr>
        </edge>
        <edge to="n3" from="n3">
            <attr name="label">
                <string>flag:o</string>
            </attr>
        </edge>
        <edge to="n2" from="n3">
            <attr name="label">
                <string>c</string>
            </attr>
        </edge>
        <edge to="n6" from="n3">
            <attr name="label">
                <string>c</string>
            </attr>
        </edge>
        <edge to="n5" from="n3">
            <attr name="label">
                <string>c</string>
            </attr>
            <attr name="layout">
                <string>500 0 448 207 513 201 543 142 12</string>
            </attr>
        </edge>
        <edge to="n4" from="n4">
            <attr name="label">
                <string>type:L</string>
            </attr>
        </edge>
        <edge to="n4" from="n4">
            <attr name="label">
                <string>flag:i</string>
            </attr>
        </edge>
        <edge to="n1" from="n4">
            <attr name="label">
                <string>c</string>
            </attr>
        </edge>
        <edge to="n9" from="n4">
            <attr name="label">
                <string>c</string>
            </attr>
        </edge>
        <edge to="n7" from="n4">
            <attr name="label">
                <string>c</string>
            </attr>
            <attr name="layout">
                <string>500 0 119 235 92 179 32 174 12</string>
            </attr>
        </edge>
        <edge to="n5" from="n5">
            <attr name="label">
                <string>type:L</string>
            </attr>
        </edge>
        <edge to="n5" from="n5">
            <attr name="label">
                <string>flag:o</string>
            </attr>
        </edge>
        <edge to="n6" from="n5">
            <attr name="label">
                <string>c</string>
            </attr>
        </edge>
        <edge to="n3" from="n5">
            <attr name="label">
                <string>c</string>
            </attr>
        </edge>
        <edge to="n2" from="n5">
            <attr name="label">
                <string>c</string>
            </attr>
        </edge>
        <edge to="n7" from="n7">
            <attr name="label">
                <string>type:L</string>
            </attr>
        </edge>
        <edge to="n7" from="n7">
            <attr name="label">
                <string>flag:i</string>
            </attr>
        </edge>
        <edge to="n9" from="n7">
            <attr name="label">
                <string>c</string>
            </attr>
        </edge>
        <edge to="n4" from="n7">
            <attr name="label">
                <string>c</string>
            </attr>
        </edge>
        <edge to="n1" from="n7">
            <attr name="label">
                <string>c</string>
            </attr>
        </edge>
    </graph>
</gxl>
