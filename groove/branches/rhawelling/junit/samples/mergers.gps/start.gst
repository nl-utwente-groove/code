<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<gxl xmlns="http://www.gupro.de/GXL/gxl-1.0.dtd">
    <graph edgemode="directed" edgeids="false" role="graph" id="start">
        <attr name="$version">
            <string>curly</string>
        </attr>
        <attr name="$version">
            <string>curly</string>
        </attr>
        <node id="n1">
            <attr name="layout">
                <string>318 186 14 18</string>
            </attr>
        </node>
        <node id="n2">
            <attr name="layout">
                <string>120 174 14 18</string>
            </attr>
        </node>
        <node id="n0">
            <attr name="layout">
                <string>216 234 13 18</string>
            </attr>
        </node>
        <node id="n4">
            <attr name="layout">
                <string>252 108 14 18</string>
            </attr>
        </node>
        <node id="n3">
            <attr name="layout">
                <string>144 78 14 18</string>
            </attr>
        </node>
        <edge to="n1" from="n1">
            <attr name="label">
                <string>flag:b</string>
            </attr>
        </edge>
        <edge to="n0" from="n2">
            <attr name="label">
                <string>a_to_c</string>
            </attr>
            <attr name="layout">
                <string>500 -4 127 183 216 238 11</string>
            </attr>
        </edge>
        <edge to="n2" from="n2">
            <attr name="label">
                <string>flag:a</string>
            </attr>
        </edge>
        <edge to="n0" from="n0">
            <attr name="label">
                <string>flag:c</string>
            </attr>
        </edge>
        <edge to="n1" from="n0">
            <attr name="label">
                <string>c_to_b</string>
            </attr>
            <attr name="layout">
                <string>396 1 222 243 318 198 11</string>
            </attr>
        </edge>
        <edge to="n1" from="n4">
            <attr name="label">
                <string>b_to_b</string>
            </attr>
        </edge>
        <edge to="n4" from="n4">
            <attr name="label">
                <string>flag:b</string>
            </attr>
        </edge>
        <edge to="n2" from="n3">
            <attr name="label">
                <string>a_to_a</string>
            </attr>
        </edge>
        <edge to="n4" from="n3">
            <attr name="label">
                <string>a_to_b</string>
            </attr>
        </edge>
        <edge to="n3" from="n3">
            <attr name="label">
                <string>flag:a</string>
            </attr>
        </edge>
    </graph>
</gxl>
