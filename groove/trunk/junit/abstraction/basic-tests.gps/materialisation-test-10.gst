<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<gxl xmlns="http://www.gupro.de/GXL/gxl-1.0.dtd">
    <graph edgemode="directed" edgeids="false" role="graph" id="materialisation-test-10">
        <attr name="$version">
            <string>curly</string>
        </attr>
        <attr name="$version">
            <string>curly</string>
        </attr>
        <node id="n0">
            <attr name="layout">
                <string>66 207 36 48</string>
            </attr>
        </node>
        <node id="n1">
            <attr name="layout">
                <string>213 138 36 48</string>
            </attr>
        </node>
        <node id="n3">
            <attr name="layout">
                <string>335 142 36 48</string>
            </attr>
        </node>
        <node id="n4">
            <attr name="layout">
                <string>327 305 36 48</string>
            </attr>
        </node>
        <node id="n6">
            <attr name="layout">
                <string>213 47 36 48</string>
            </attr>
        </node>
        <node id="n7">
            <attr name="layout">
                <string>336 56 36 48</string>
            </attr>
        </node>
        <node id="n8">
            <attr name="layout">
                <string>331 404 36 48</string>
            </attr>
        </node>
        <edge to="n0" from="n0">
            <attr name="label">
                <string>type:B</string>
            </attr>
        </edge>
        <edge to="n1" from="n0">
            <attr name="label">
                <string>f</string>
            </attr>
        </edge>
        <edge to="n1" from="n1">
            <attr name="label">
                <string>type:C</string>
            </attr>
        </edge>
        <edge to="n6" from="n1">
            <attr name="label">
                <string>v</string>
            </attr>
        </edge>
        <edge to="n3" from="n1">
            <attr name="label">
                <string>n</string>
            </attr>
        </edge>
        <edge to="n3" from="n3">
            <attr name="label">
                <string>type:C</string>
            </attr>
        </edge>
        <edge to="n4" from="n3">
            <attr name="label">
                <string>n</string>
            </attr>
        </edge>
        <edge to="n7" from="n3">
            <attr name="label">
                <string>v</string>
            </attr>
        </edge>
        <edge to="n4" from="n4">
            <attr name="label">
                <string>type:C</string>
            </attr>
        </edge>
        <edge to="n8" from="n4">
            <attr name="label">
                <string>v</string>
            </attr>
        </edge>
        <edge to="n1" from="n4">
            <attr name="label">
                <string>n</string>
            </attr>
        </edge>
        <edge to="n6" from="n6">
            <attr name="label">
                <string>type:O</string>
            </attr>
        </edge>
        <edge to="n7" from="n7">
            <attr name="label">
                <string>type:O</string>
            </attr>
        </edge>
        <edge to="n8" from="n8">
            <attr name="label">
                <string>type:O</string>
            </attr>
        </edge>
    </graph>
</gxl>
