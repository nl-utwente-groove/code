<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<gxl xmlns="http://www.gupro.de/GXL/gxl-1.0.dtd">
    <graph role="graph" edgeids="false" edgemode="directed" id="crit">
        <attr name="$version">
            <string>curly</string>
        </attr>
        <attr name="$version">
            <string>curly</string>
        </attr>
        <node id="n6">
            <attr name="layout">
                <string>191 268 19 19</string>
            </attr>
        </node>
        <node id="n2">
            <attr name="layout">
                <string>130 270 19 19</string>
            </attr>
        </node>
        <node id="n3">
            <attr name="layout">
                <string>149 166 45 36</string>
            </attr>
        </node>
        <node id="n4">
            <attr name="layout">
                <string>110 77 20 18</string>
            </attr>
        </node>
        <node id="n0">
            <attr name="layout">
                <string>88 269 19 19</string>
            </attr>
        </node>
        <node id="n1">
            <attr name="layout">
                <string>24 268 19 19</string>
            </attr>
        </node>
        <node id="n5">
            <attr name="layout">
                <string>41 160 45 36</string>
            </attr>
        </node>
        <edge from="n3" to="n3">
            <attr name="label">
                <string>append</string>
            </attr>
        </edge>
        <edge from="n3" to="n3">
            <attr name="label">
                <string>control</string>
            </attr>
        </edge>
        <edge from="n3" to="n6">
            <attr name="label">
                <string>x</string>
            </attr>
        </edge>
        <edge from="n3" to="n2">
            <attr name="label">
                <string>caller</string>
            </attr>
            <attr name="layout">
                <string>512 0 164 205 143 269 11</string>
            </attr>
        </edge>
        <edge from="n3" to="n4">
            <attr name="label">
                <string>this</string>
            </attr>
        </edge>
        <edge from="n4" to="n4">
            <attr name="label">
                <string>last</string>
            </attr>
        </edge>
        <edge from="n5" to="n5">
            <attr name="label">
                <string>append</string>
            </attr>
        </edge>
        <edge from="n5" to="n5">
            <attr name="label">
                <string>control</string>
            </attr>
        </edge>
        <edge from="n5" to="n1">
            <attr name="label">
                <string>caller</string>
            </attr>
            <attr name="layout">
                <string>498 3 57 199 36 267 11</string>
            </attr>
        </edge>
        <edge from="n5" to="n4">
            <attr name="label">
                <string>this</string>
            </attr>
        </edge>
        <edge from="n5" to="n0">
            <attr name="label">
                <string>x</string>
            </attr>
        </edge>
    </graph>
</gxl>
