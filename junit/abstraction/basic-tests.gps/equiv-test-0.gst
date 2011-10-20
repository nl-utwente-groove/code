<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<gxl xmlns="http://www.gupro.de/GXL/gxl-1.0.dtd">
    <graph edgemode="directed" edgeids="false" role="graph" id="equiv-test-0">
        <attr name="$version">
            <string>curly</string>
        </attr>
        <attr name="$version">
            <string>curly</string>
        </attr>
        <node id="n2">
            <attr name="layout">
                <string>243 176 50 22</string>
            </attr>
        </node>
        <node id="n1">
            <attr name="layout">
                <string>337 81 50 38</string>
            </attr>
        </node>
        <node id="n0">
            <attr name="layout">
                <string>241 78 50 54</string>
            </attr>
        </node>
        <node id="n4">
            <attr name="layout">
                <string>433 173 50 54</string>
            </attr>
        </node>
        <node id="n3">
            <attr name="layout">
                <string>332 175 45 22</string>
            </attr>
        </node>
        <edge to="n1" from="n2">
            <attr name="label">
                <string>diff</string>
            </attr>
        </edge>
        <edge to="n0" from="n0">
            <attr name="label">
                <string>flag:C</string>
            </attr>
        </edge>
        <edge to="n0" from="n0">
            <attr name="label">
                <string>flag:B</string>
            </attr>
        </edge>
        <edge to="n1" from="n1">
            <attr name="label">
                <string>flag:B</string>
            </attr>
        </edge>
        <edge to="n1" from="n1">
            <attr name="label">
                <string>type:A</string>
            </attr>
        </edge>
        <edge to="n4" from="n4">
            <attr name="label">
                <string>flag:B</string>
            </attr>
        </edge>
        <edge to="n4" from="n4">
            <attr name="label">
                <string>flag:C</string>
            </attr>
        </edge>
        <edge to="n0" from="n0">
            <attr name="label">
                <string>type:A</string>
            </attr>
        </edge>
        <edge to="n3" from="n3">
            <attr name="label">
                <string>flag:B</string>
            </attr>
        </edge>
        <edge to="n1" from="n0">
            <attr name="label">
                <string>equal</string>
            </attr>
        </edge>
        <edge to="n4" from="n4">
            <attr name="label">
                <string>type:A</string>
            </attr>
        </edge>
        <edge to="n2" from="n2">
            <attr name="label">
                <string>type:A</string>
            </attr>
        </edge>
        <edge to="n4" from="n3">
            <attr name="label">
                <string>link</string>
            </attr>
        </edge>
    </graph>
</gxl>
