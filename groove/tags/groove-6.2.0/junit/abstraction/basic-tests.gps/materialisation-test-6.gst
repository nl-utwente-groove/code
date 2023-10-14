<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<gxl xmlns="http://www.gupro.de/GXL/gxl-1.0.dtd">
    <graph edgemode="directed" edgeids="false" role="graph" id="materialisation-test-6">
        <attr name="$version">
            <string>curly</string>
        </attr>
        <node id="n1">
            <attr name="layout">
                <string>39 152 29 32</string>
            </attr>
        </node>
        <node id="n0">
            <attr name="layout">
                <string>102 65 29 32</string>
            </attr>
        </node>
        <node id="n2">
            <attr name="layout">
                <string>163 145 29 32</string>
            </attr>
        </node>
        <edge to="n1" from="n2">
            <attr name="label">
                <string>n</string>
            </attr>
        </edge>
        <edge to="n2" from="n0">
            <attr name="label">
                <string>n</string>
            </attr>
        </edge>
        <edge to="n0" from="n1">
            <attr name="label">
                <string>n</string>
            </attr>
        </edge>
        <edge to="n2" from="n1">
            <attr name="label">
                <string>n</string>
            </attr>
            <attr name="layout">
                <string>500 0 54 168 118 199 178 161 12</string>
            </attr>
        </edge>
        <edge to="n2" from="n2">
            <attr name="label">
                <string>type:C</string>
            </attr>
        </edge>
        <edge to="n0" from="n2">
            <attr name="label">
                <string>n</string>
            </attr>
            <attr name="layout">
                <string>500 0 178 161 170 103 117 81 12</string>
            </attr>
        </edge>
        <edge to="n1" from="n0">
            <attr name="label">
                <string>n</string>
            </attr>
            <attr name="layout">
                <string>500 0 117 81 60 106 54 168 12</string>
            </attr>
        </edge>
        <edge to="n1" from="n1">
            <attr name="label">
                <string>type:C</string>
            </attr>
        </edge>
        <edge to="n0" from="n0">
            <attr name="label">
                <string>type:C</string>
            </attr>
        </edge>
    </graph>
</gxl>
